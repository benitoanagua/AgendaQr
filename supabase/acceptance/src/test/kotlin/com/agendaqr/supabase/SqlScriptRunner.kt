package com.agendaqr.supabase

import java.sql.Connection
import java.sql.Statement

/**
 * Ejecutor de scripts SQL puro (equivalente a psql -v ON_ERROR_STOP=1).
 * Divide sentencias respetando comillas simples ('), comentarios (-- y /* */)
 * y dollar-quoting ($$ y $tag$). Cada sentencia se ejecuta en autocommit;
 * la primera que falla aborta con el archivo y el índice de la sentencia.
 */
object SqlScriptRunner {

    data class Statement_(val sql: String, val index: Int)

    /**
     * Divide el contenido SQL en sentencias individuales.
     * Respeta: 'string literals', "quoted identifiers", $$dollar$$ blocks,
     * $tag$..$tag$ blocks, -- line comments, /* block comments */.
     */
    fun splitStatements(sql: String): List<String> {
        val statements = mutableListOf<String>()
        val current = StringBuilder()
        var i = 0
        val len = sql.length

        while (i < len) {
            val c = sql[i]
            when {
                // Line comment
                c == '-' && i + 1 < len && sql[i + 1] == '-' -> {
                    while (i < len && sql[i] != '\n') { current.append(sql[i]); i++ }
                }
                // Block comment
                c == '/' && i + 1 < len && sql[i + 1] == '*' -> {
                    while (i + 1 < len && !(sql[i] == '*' && sql[i + 1] == '/')) { current.append(sql[i]); i++ }
                    if (i + 1 < len) { current.append(sql[i]); current.append(sql[i + 1]); i += 2 }
                }
                // Single-quoted string
                c == '\'' -> {
                    current.append(c); i++
                    while (i < len) {
                        current.append(sql[i])
                        if (sql[i] == '\'' && i + 1 < len && sql[i + 1] == '\'') {
                            current.append(sql[i + 1]); i += 2; continue
                        }
                        if (sql[i] == '\'') { i++; break }
                        i++
                    }
                }
                // Double-quoted identifier
                c == '"' -> {
                    current.append(c); i++
                    while (i < len && sql[i] != '"') { current.append(sql[i]); i++ }
                    if (i < len) { current.append('"'); i++ }
                }
                // Dollar-quoted block ($$...$$ or $tag$...$tag$)
                c == '$' -> {
                    val tagEnd = findDollarTagEnd(sql, i)
                    if (tagEnd > i) {
                        val tag = sql.substring(i, tagEnd + 1) // $tag$ or $$
                        current.append(tag)
                        val closeIdx = sql.indexOf(tag, tagEnd + 1)
                        if (closeIdx >= 0) {
                            current.append(sql.substring(tagEnd + 1, closeIdx + tag.length))
                            i = closeIdx + tag.length
                        } else {
                            current.append(sql.substring(tagEnd + 1))
                            i = len
                        }
                    } else {
                        current.append(c); i++
                    }
                }
                // Statement terminator
                c == ';' -> {
                    val trimmed = current.toString().trim()
                    if (trimmed.isNotEmpty() && !isOnlyComments(trimmed)) {
                        statements.add(trimmed + ";")
                    }
                    current.clear()
                    i++
                }
                else -> {
                    current.append(c); i++
                }
            }
        }
        // Trailing content without semicolon (last statement)
        val trailing = current.toString().trim()
        if (trailing.isNotEmpty() && !isOnlyComments(trailing)) {
            statements.add(trailing)
        }
        return statements
    }

    private fun findDollarTagEnd(sql: String, start: Int): Int {
        // $$ or $tag$
        if (start + 1 < sql.length && sql[start + 1] == '$') return start + 1 // $$
        // $tag$ — find closing $
        var i = start + 1
        while (i < sql.length && (sql[i].isLetterOrDigit() || sql[i] == '_')) i++
        if (i < sql.length && sql[i] == '$') return i
        return -1
    }

    private fun isOnlyComments(text: String): Boolean {
        return text.lines().all { line ->
            val t = line.trim()
            t.isEmpty() || t.startsWith("--") || t.startsWith("/*") || t.endsWith("*/")
        }
    }

    /**
     * Ejecuta un archivo SQL con fail-fast: cada sentencia se ejecuta en
     * autocommit y la primera que falla aborta con contexto claro.
     */
    fun executeFile(conn: Connection, file: java.io.File): Result {
        val sql = file.readText()
        val statements = splitStatements(sql)
        conn.autoCommit = true
        conn.createStatement().use { stmt ->
            statements.forEachIndexed { index, statement ->
                try {
                    stmt.execute(statement)
                } catch (e: java.sql.SQLException) {
                    return Result.Error(
                        file = file.name,
                        statementIndex = index,
                        sql = statement.take(200),
                        cause = e.message ?: "?",
                    )
                }
            }
        }
        return Result.Success(statements.size)
    }

    sealed class Result {
        data class Success(val statements: Int) : Result()
        data class Error(
            val file: String,
            val statementIndex: Int,
            val sql: String,
            val cause: String,
        ) : Result()
    }
}
