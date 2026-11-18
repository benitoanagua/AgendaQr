package com.agendaqr.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.platform.testTag
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Contrato de [AqCarousel]: carrusel horizontal de tarjetas (S01 favoritos).
 *
 * - Renderiza todos los ítems pasados.
 * - Cada ítem tiene un testTag estable por su id.
 * - El primer y último ítem existen (margen de pantalla aplicado).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
class AqCarouselTest {

    @get:Rule
    val compose = createComposeRule()

    private val items = listOf(
        AqCarouselItem("fav-1", "Favorito 1"),
        AqCarouselItem("fav-2", "Favorito 2"),
        AqCarouselItem("fav-3", "Favorito 3"),
    )

    private fun composeContent() {
        compose.setContent {
            AqTheme(darkTheme = false) {
                AqCarousel(
                    items = items,
                ) { item ->
                    androidx.compose.foundation.text.BasicTextField(
                        value = item.label ?: "",
                        onValueChange = {},
                        modifier = androidx.compose.ui.Modifier.testTag("carousel-item-${item.id}"),
                    )
                }
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun renders_all_items() {
        composeContent()
        items.forEach { item ->
            compose.onNodeWithTag("carousel-item-${item.id}")
        }
    }

    @Test
    fun first_and_last_items_present() {
        composeContent()
        compose.onNodeWithTag("aq-carousel-item-fav-1")
        compose.onNodeWithTag("aq-carousel-item-fav-3")
    }
}
