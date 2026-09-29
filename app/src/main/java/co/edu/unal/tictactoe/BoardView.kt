package co.edu.unal.tictactoe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Vista personalizada para el tablero de Tic-Tac-Toe.
 *
 * Reto 5:
 * - El tablero se dibuja utilizando Canvas.
 * - Las X y O también se dibujan sobre Canvas.
 * - Se detectan los toques del usuario.
 */
class BoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // -------------------------------------------------
    // 1. ESTADO DEL TABLERO
    // -------------------------------------------------

    // Nueve posiciones:
    // 0 1 2
    // 3 4 5
    // 6 7 8
    private val board = CharArray(9) { ' ' }

    // Función que FirstFragment podrá ejecutar
    // cuando el usuario toque una casilla.
    var onCellClickListener: ((Int) -> Unit)? = null


    // -------------------------------------------------
    // 2. PINTURA DE LA CUADRÍCULA
    // -------------------------------------------------

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }


    // -------------------------------------------------
    // 3. PINTURA DE LA X
    // -------------------------------------------------

    private val xPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(0, 170, 0)
        strokeWidth = 12f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }


    // -------------------------------------------------
    // 4. PINTURA DE LA O
    // -------------------------------------------------

    private val oPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(200, 0, 0)
        strokeWidth = 12f
        style = Paint.Style.STROKE
    }


    // -------------------------------------------------
    // 5. DIBUJAR EL TABLERO
    // -------------------------------------------------

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val boardWidth = width.toFloat()
        val boardHeight = height.toFloat()

        val cellWidth = boardWidth / 3f
        val cellHeight = boardHeight / 3f


        // -----------------------------
        // LÍNEAS VERTICALES
        // -----------------------------

        canvas.drawLine(
            cellWidth,
            0f,
            cellWidth,
            boardHeight,
            gridPaint
        )

        canvas.drawLine(
            cellWidth * 2,
            0f,
            cellWidth * 2,
            boardHeight,
            gridPaint
        )


        // -----------------------------
        // LÍNEAS HORIZONTALES
        // -----------------------------

        canvas.drawLine(
            0f,
            cellHeight,
            boardWidth,
            cellHeight,
            gridPaint
        )

        canvas.drawLine(
            0f,
            cellHeight * 2,
            boardWidth,
            cellHeight * 2,
            gridPaint
        )


        // -----------------------------
        // DIBUJAR X Y O
        // -----------------------------

        for (position in board.indices) {

            val row = position / 3
            val column = position % 3

            val left = column * cellWidth
            val top = row * cellHeight

            val right = left + cellWidth
            val bottom = top + cellHeight

            val padding = cellWidth * 0.22f

            when (board[position]) {

                'X' -> {

                    // Primera línea de la X
                    canvas.drawLine(
                        left + padding,
                        top + padding,
                        right - padding,
                        bottom - padding,
                        xPaint
                    )

                    // Segunda línea de la X
                    canvas.drawLine(
                        right - padding,
                        top + padding,
                        left + padding,
                        bottom - padding,
                        xPaint
                    )
                }

                'O' -> {

                    val centerX = left + cellWidth / 2f
                    val centerY = top + cellHeight / 2f

                    val radius =
                        minOf(cellWidth, cellHeight) * 0.28f

                    canvas.drawCircle(
                        centerX,
                        centerY,
                        radius,
                        oPaint
                    )
                }
            }
        }
    }


    // -------------------------------------------------
    // 6. DETECTAR TOQUES DEL USUARIO
    // -------------------------------------------------

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action != MotionEvent.ACTION_UP) {
            return true
        }

        val cellWidth = width / 3f
        val cellHeight = height / 3f

        val column = (event.x / cellWidth).toInt()
        val row = (event.y / cellHeight).toInt()

        if (column !in 0..2 || row !in 0..2) {
            return true
        }

        val position = row * 3 + column

        // Informamos a FirstFragment qué casilla
        // fue seleccionada.
        onCellClickListener?.invoke(position)

        performClick()

        return true
    }


    // -------------------------------------------------
    // 7. ACCESIBILIDAD
    // -------------------------------------------------

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }


    // -------------------------------------------------
    // 8. COLOCAR UNA X O UNA O
    // -------------------------------------------------

    fun setCell(position: Int, player: Char) {

        if (position !in 0..8) {
            return
        }

        board[position] = player

        // Obliga a Android a volver a ejecutar onDraw().
        invalidate()
    }


    // -------------------------------------------------
    // 9. CONSULTAR UNA CASILLA
    // -------------------------------------------------

    fun getCell(position: Int): Char {

        if (position !in 0..8) {
            return ' '
        }

        return board[position]
    }


    // -------------------------------------------------
    // 10. LIMPIAR EL TABLERO
    // -------------------------------------------------

    fun clearBoard() {

        for (i in board.indices) {
            board[i] = ' '
        }

        invalidate()
    }
}