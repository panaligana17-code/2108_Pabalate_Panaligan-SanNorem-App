package com.sannorem.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

// Four exact brand colors from prompt
val LogoThemeBlue = Color(0xFF0084D6)        // Bright Blue - Main Theme
val LogoDaliPink = Color(0xFFE91E63)         // Distinct Pink - Dali
val LogoSevenElevenGreen = Color(0xFF008657) // Vibrant Green - 7-Eleven
val LogoAlfaRed = Color(0xFFED1C24)          // Bold Red - Alfa
val LogoWhite = Color(0xFFFFFFFF)

@Composable
fun SanNoremLogo(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier.background(LogoWhite)
    ) {
        val w = size.width
        val scale = w / 108f

        fun sx(x: Float) = x * scale
        fun sy(y: Float) = y * scale

        val cx = 54f
        val cy = 39f
        val rOuter = 26f
        val rInner = 14f

        fun polarPt(r: Float, deg: Float): Offset {
            val rad = Math.toRadians(deg.toDouble())
            return Offset(
                sx(cx + (r * cos(rad)).toFloat()),
                sy(cy - (r * sin(rad)).toFloat())
            )
        }

        fun Path.addSmoothArc(r: Float, startDeg: Float, endDeg: Float, steps: Int = 16) {
            for (i in 0..steps) {
                val t = i.toFloat() / steps
                val angle = startDeg + ((endDeg - startDeg) * t)
                val pt = polarPt(r, angle)
                if (i == 0) lineTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
            }
        }

        // 4 Branded Awning Canopy Panels across the Question Mark arch
        // Each panel has an exact 45° span, separated by a clean 3° white groove.
        val panels = listOf(
            Triple(189f, 144f, LogoThemeBlue),        // Panel 1: Bright Blue (Main Theme)
            Triple(141f, 96f, LogoDaliPink),          // Panel 2: Distinct Pink (Dali)
            Triple(93f, 48f, LogoSevenElevenGreen),   // Panel 3: Vibrant Green (7-Eleven)
            Triple(45f, 0f, LogoAlfaRed)              // Panel 4: Bold Red (Alfa)
        )

        panels.forEach { (startDeg, endDeg, color) ->
            val p = Path().apply {
                val pOuterStart = polarPt(rOuter, startDeg)
                moveTo(pOuterStart.x, pOuterStart.y)

                // Outer circular curve
                addSmoothArc(rOuter, startDeg, endDeg)

                // Outer edge to inner edge
                val pInnerEnd = polarPt(rInner, endDeg)
                lineTo(pInnerEnd.x, pInnerEnd.y)

                // Inner circular curve back
                addSmoothArc(rInner, endDeg, startDeg)

                // Close back to outer start with clean radial line
                close()
            }
            drawPath(p, color = color, style = Fill)
        }

        // Store Facade / Question Mark Stem (Bright Blue)
        // Seamlessly continues from angle 0° down into the central vertical store column
        val pathStem = Path().apply {
            val startOuter = polarPt(rOuter, 0f)  // (80, 39)
            val startInner = polarPt(rInner, 0f)  // (68, 39)

            moveTo(startInner.x, startInner.y)
            lineTo(startOuter.x, startOuter.y)

            // Outer contour: curves down around right shoulder and sweeps in toward stem
            cubicTo(
                sx(80f), sy(52f),
                sx(69f), sy(56f),
                sx(61f), sy(59f)
            )
            // Vertical outer right wall of store stem
            lineTo(sx(61f), sy(68f))
            // Bottom floor of stem
            lineTo(sx(47f), sy(68f))
            // Vertical left wall of store stem
            lineTo(sx(47f), sy(59f))
            // Inner contour: sweeps smoothly up into the inner loop
            cubicTo(
                sx(47f), sy(52f),
                sx(56f), sy(47f),
                startInner.x, startInner.y
            )
            close()
        }
        drawPath(pathStem, color = LogoThemeBlue, style = Fill)

        // Store Display Window on the Stem (Clean White Glass Pane)
        val winLeft = sx(49.5f)
        val winTop = sy(59.5f)
        val winW = sx(9f)
        val winH = sy(6.5f)
        drawRoundRect(
            color = LogoWhite,
            topLeft = Offset(winLeft, winTop),
            size = Size(winW, winH),
            cornerRadius = CornerRadius(sx(1.5f), sx(1.5f))
        )
        // Store window divider bars (Mullions in Bright Blue)
        drawLine(
            color = LogoThemeBlue.copy(alpha = 0.6f),
            start = Offset(winLeft + winW / 2f, winTop),
            end = Offset(winLeft + winW / 2f, winTop + winH),
            strokeWidth = sx(0.8f)
        )
        drawLine(
            color = LogoThemeBlue.copy(alpha = 0.6f),
            start = Offset(winLeft, winTop + winH / 2f),
            end = Offset(winLeft + winW, winTop + winH / 2f),
            strokeWidth = sx(0.8f)
        )

        // The Question Mark Dot - Retail Store Entrance (Bright Blue)
        val dotRadius = sx(7.5f)
        val dotCenter = Offset(sx(54f), sy(80.5f))
        drawCircle(
            color = LogoThemeBlue,
            radius = dotRadius,
            center = dotCenter
        )

        // Arched Store Door inside the Dot (Clean White)
        val doorW = sx(5.6f)
        val doorH = sy(8.5f)
        val doorLeft = sx(51.2f)
        val doorBottom = sy(86.5f)
        val doorTop = doorBottom - doorH

        val pathDoor = Path().apply {
            moveTo(doorLeft, doorBottom)
            lineTo(doorLeft, doorTop + sx(2.6f))
            cubicTo(
                doorLeft, doorTop,
                doorLeft + doorW, doorTop,
                doorLeft + doorW, doorTop + sx(2.6f)
            )
            lineTo(doorLeft + doorW, doorBottom)
            close()
        }
        drawPath(pathDoor, color = LogoWhite, style = Fill)

        // Door handle knob in Bright Blue
        drawCircle(
            color = LogoThemeBlue,
            radius = sx(0.6f),
            center = Offset(doorLeft + sx(4.2f), doorTop + sx(4.5f))
        )
    }
}

@Preview(showBackground = true, widthDp = 200, heightDp = 200)
@Composable
fun SanNoremLogoPreview() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.White)
    ) {
        SanNoremLogo(modifier = Modifier.fillMaxSize())
    }
}
