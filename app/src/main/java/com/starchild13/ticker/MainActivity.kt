package com.starchild13.ticker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import kotlin.with as with1

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                TickerBoard(text = "Hello World", numColumns = 6, numRows =2)
            }
        }
    }
}



@Composable
fun TextBox(letter: String,
            backgroundColor: Color,
            modifier: Modifier = Modifier) {
    Box(

    ) {
        Text(
                text = letter.toString(),
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 60.sp,
                modifier = modifier
                    .background(backgroundColor)
                    .drawBehind {
                        drawLine(
                            Color.Red,
                            Offset(x = 0f, y = center.y),
                            Offset(
                                x = size.width,
                                y = center.y
                            ),
                            strokeWidth = density,
                        )
                    },
                onTextLayout = { textLayoutResult ->
                    // 1
                    val layoutInput = textLayoutResult.layoutInput
                    // 2
                    val fontSizePx = with1(layoutInput.density) { layoutInput.style.fontSize.toPx() }
                    // 3
                    val baseline = textLayoutResult.firstBaseline
                    // 4
                    val top = textLayoutResult.getLineTop(0)
                    // 5
                    val bottom = textLayoutResult.getLineBottom(0)
                    // 6
                    val ascent = bottom - fontSizePx
                    // 7
                    val descent = bottom - (baseline - fontSizePx - top)
                }
            )


    }
}


@Composable
fun CenteredText(
    letter: Char,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    backgroundColor: Color = Color.Black,
    fontSize: TextUnit = 96.sp,
) {
    // 1
    var ascent by remember {
        mutableFloatStateOf(0f)
    }
    var middle by remember {
        mutableFloatStateOf(0f)
    }
    var baseline by remember {
        mutableFloatStateOf(0f)
    }
    var top by remember {
        mutableFloatStateOf(0f)
    }
    var bottom by remember {
        mutableFloatStateOf(0f)
    }
    // 2
    val delta: Float by remember {
        derivedStateOf {
            ((bottom - baseline) - (ascent - top)) / 2f
        }
    }

    Text(
        text = letter.toString(),
        color = textColor,
        fontFamily = FontFamily.Monospace,
        fontSize = fontSize,
        modifier = modifier
            .background(backgroundColor)
            .drawBehind {
                drawLine(
                    textColor,
                    Offset(x = 0f, y = center.y),
                    Offset(x = size.width, y = center.y),
                    strokeWidth = 2f * density,
                )
            }
            // 3
            .offset {
                IntOffset(x = 0, y = delta.roundToInt())
            },
        onTextLayout = { textLayoutResult ->
            val layoutInput = textLayoutResult.layoutInput
            val fontSizePx = with1(layoutInput.density) { layoutInput.style.fontSize.toPx() }
            baseline = textLayoutResult.firstBaseline
            top = textLayoutResult.getLineTop(0)
            bottom = textLayoutResult.getLineBottom(0)
            middle = bottom - top
            ascent = bottom - fontSizePx
        }
    )
}

    @Composable
    fun TopHalf(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        // 1
        Layout(
            // 2
            modifier = modifier.clipToBounds(),
            content = content,
        ) { measurables, constraints ->
            require(measurables.size == 1) { "This composable expects a single child" }

            // 3
            val placeable = measurables.first().measure(constraints)
            // 4
            val height = placeable.height / 2

            // 5
            layout(
                width = placeable.width,
                height = height,
            ) {
                // 6
                placeable.placeRelative(
                    x = 0,
                    y = 0,
                )
            }
        }
    }


    @Composable
    fun BottomHalf(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        Layout(
            modifier = modifier.clipToBounds(),
            content = content,
        ) { measurables, constraints ->
            require(measurables.size == 1) { "This composable expects a single child" }

            val placeable = measurables.first().measure(constraints)
            val height = placeable.height / 2

            layout(
                width = placeable.width,
                height = height,
            ) {
                placeable.placeRelative(
                    x = 0,
                    // 1
                    y = -height,
                )
            }
        }
    }



@Composable
fun Ticker(
    letter: Char,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    backgroundColor: Color = Color.Black,
    fontSize: TextUnit = 96.sp,
) {
    // 1
    val animatable = remember {
        Animatable(initialValue = 0f)
    }
    // 2
    LaunchedEffect(key1 = letter) {
        // 3
        val currentIndex = animatable.value.toInt()
        // 4
        val index = AlphabetMapper.getIndexOf(letter)
        // 5
        val target = if (index < currentIndex) {
            index + (AlphabetMapper.size * (currentIndex / AlphabetMapper.size + 1))
        } else {
            index
        }
        // 6
        val TickerCycleMillis = 1000
        val result = animatable.animateTo(
            targetValue = target.toFloat(),
            animationSpec = tween(
                durationMillis = (target - currentIndex) * TickerCycleMillis,
                easing = FastOutSlowInEasing,
            )
        )
        // 7
        if (result.endReason == AnimationEndReason.Finished) {
            animatable.snapTo(index.toFloat())
        }
    }
    // 8
    val fraction = animatable.value - animatable.value.toInt()
    // 9
    val rotation = -180f * fraction
    // 10
    val currentLetter = AlphabetMapper.getLetterAt(animatable.value.toInt())
    val nextLetter = AlphabetMapper.getLetterAt(animatable.value.toInt() + 1)
    Box(
        modifier = modifier
    ) {
        CenteredText(
            letter = nextLetter,
            textColor = textColor,
            backgroundColor = backgroundColor,
            fontSize = fontSize,
        )
        Column(
        ) {
            Box(
                modifier = Modifier
                    .zIndex(1f)
                    // 11
                    .graphicsLayer {
                        rotationX = rotation
                        cameraDistance = 6f * density
                        transformOrigin = TransformOrigin(.5f, 1f)
                    }

            ) {
                if (fraction <= .5f) {
                    TopHalf {
                        CenteredText(
                            letter = currentLetter,
                            textColor = textColor,
                            backgroundColor = backgroundColor,
                            fontSize = fontSize,
                        )
                    }
                } else {
                    BottomHalf(
                        modifier = Modifier.graphicsLayer {
                            rotationX = 180f
                        }
                    ) {
                        CenteredText(
                            letter = nextLetter,
                            textColor = textColor,
                            backgroundColor = backgroundColor,
                            fontSize = fontSize,
                        )
                    }
                }
            }
            BottomHalf {
                CenteredText(
                    letter = currentLetter,
                    textColor = textColor,
                    backgroundColor = backgroundColor,
                    fontSize = fontSize,
                )
            }
        }
    }
}
@Composable
fun TickerRow(
    // 1
    text: String,
    // 2
    numCells: Int,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    backgroundColor: Color = Color.Black,
    fontSize: TextUnit = 96.sp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
) {
    // 3
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement
    ) {
        // 4
        repeat(numCells) { index ->
            // 5
            Ticker(
                letter = text.getOrNull(index) ?: ' ',
                textColor = textColor,
                backgroundColor = backgroundColor,
                fontSize = fontSize
            )
        }
    }
}

@Composable
fun TickerBoard(
    // 1
    text: String,
    // 2
    numColumns: Int,
    // 3
    numRows: Int,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    backgroundColor: Color = Color.Black,
    fontSize: TextUnit = 96.sp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),


) {
    // 4
    val padded = text.padEnd(numColumns * numRows, ' ')
    // 5
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
    ) {
        // 6
        repeat(numRows) { row ->
            TickerRow(
                text = padded.substring(startIndex = row * numColumns),
                numCells = numColumns,
                horizontalArrangement = horizontalArrangement,
                textColor = textColor,
                backgroundColor = backgroundColor,
                fontSize = fontSize,
            )
        }
    }
  }

