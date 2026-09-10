package co.edu.unal.tictactoe

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import co.edu.unal.tictactoe.databinding.FragmentFirstBinding


/**
 * FirstFragment controla la parte visual del juego.
 *
 * Aquí conectamos:
 *
 * - Los nueve botones del tablero.
 * - El texto que informa el estado de la partida.
 * - El botón "New Game".
 *
 * La lógica del juego permanece separada
 * en la clase TicTacToeGame.
 */
class FirstFragment : Fragment() {


    // ============================================================
    // 1. VIEW BINDING
    // ============================================================

    // _binding permite acceder a los elementos de fragment_first.xml.
    private var _binding: FragmentFirstBinding? = null

    // Esta propiedad facilita utilizar binding dentro del Fragment.
    private val binding get() = _binding!!


    // ============================================================
    // 2. LÓGICA DEL JUEGO
    // ============================================================

    // Creamos una instancia del "cerebro" del Tic-Tac-Toe.
    private val game = TicTacToeGame()

    // Array que contendrá los nueve botones del tablero.
    private lateinit var boardButtons: Array<Button>

    // Indica si la partida ya terminó.
    private var gameOver = false


    // ============================================================
    // 3. CREACIÓN DE LA INTERFAZ
    // ============================================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Conectamos el Fragment con fragment_first.xml.
        _binding = FragmentFirstBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }


    // ============================================================
    // 4. CUANDO LA VISTA YA ESTÁ CREADA
    // ============================================================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)


        // --------------------------------------------------------
        // CONECTAMOS LOS 9 BOTONES DEL XML
        // --------------------------------------------------------

        boardButtons = arrayOf(

            binding.one,      // posición 0
            binding.two,      // posición 1
            binding.three,    // posición 2

            binding.four,     // posición 3
            binding.five,     // posición 4
            binding.six,      // posición 5

            binding.seven,    // posición 6
            binding.eight,    // posición 7
            binding.nine      // posición 8
        )


        // Iniciamos la primera partida.
        startNewGame()


        // --------------------------------------------------------
        // BOTÓN NEW GAME
        // --------------------------------------------------------

        binding.restartButton.setOnClickListener {

            startNewGame()
        }
    }


    // ============================================================
    // 5. INICIAR UNA NUEVA PARTIDA
    // ============================================================

    private fun startNewGame() {

        // Limpiamos el tablero interno.
        game.resetGame()

        // Indicamos que la partida está activa.
        gameOver = false


        // Recorremos los nueve botones.
        for (i in boardButtons.indices) {

            val button = boardButtons[i]

            // Borramos cualquier X u O anterior.
            button.text = ""

            // Habilitamos nuevamente la casilla.
            button.isEnabled = true

            // Dejamos el color inicial.
            button.setTextColor(Color.BLACK)


            // Cada botón conoce su posición:
            //
            // 0 | 1 | 2
            // ---------
            // 3 | 4 | 5
            // ---------
            // 6 | 7 | 8
            //
            button.setOnClickListener {

                humanMove(i)
            }
        }


        // Mostramos el mensaje inicial.
        binding.information.setText(
            R.string.first_human
        )
    }


    // ============================================================
    // 6. MOVIMIENTO DEL JUGADOR HUMANO
    // ============================================================

    private fun humanMove(position: Int) {

        // Si la partida terminó, no permitimos más jugadas.
        if (gameOver) {
            return
        }


        // Intentamos realizar la jugada.
        //
        // makeMove devuelve false si la casilla
        // ya estaba ocupada.
        val validMove = game.makeMove(position)


        if (!validMove) {
            return
        }


        // Mostramos X en la pantalla.
        boardButtons[position].text = "X"

        // X debe aparecer verde, según el reto.
        boardButtons[position].setTextColor(
            Color.rgb(0, 180, 0)
        )

        // Deshabilitamos esa posición.
        boardButtons[position].isEnabled = false


        // Revisamos si X ganó.
        var result = game.checkWinner()


        // Si la partida todavía continúa,
        // dejamos jugar al computador.
        if (result == 0) {

            computerMove()

            // Revisamos nuevamente el resultado
            // después del movimiento del computador.
            result = game.checkWinner()
        }


        // Actualizamos el texto de estado.
        updateGameStatus(result)
    }


    // ============================================================
    // 7. MOVIMIENTO DEL COMPUTADOR
    // ============================================================

    private fun computerMove() {

        // Informamos que es turno del computador.
        binding.information.setText(
            R.string.turn_computer
        )


        // TicTacToeGame decide cuál es la mejor posición.
        val position = game.getComputerMove()


        // -1 significa que ya no existen posiciones disponibles.
        if (position == -1) {
            return
        }


        // En este momento currentPlayer es O,
        // porque makeMove() cambió el turno después de la X.
        game.makeMove(position)


        // Mostramos O.
        boardButtons[position].text = "O"

        // O aparece rojo.
        boardButtons[position].setTextColor(
            Color.rgb(200, 0, 0)
        )

        // La casilla ya no puede volver a utilizarse.
        boardButtons[position].isEnabled = false
    }


    // ============================================================
    // 8. ACTUALIZAR EL RESULTADO
    // ============================================================

    private fun updateGameStatus(result: Int) {

        when (result) {

            // ----------------------------------------------------
            // 0 = la partida continúa
            // ----------------------------------------------------
            0 -> {

                binding.information.setText(
                    R.string.turn_human
                )
            }


            // ----------------------------------------------------
            // 1 = ganó X
            // ----------------------------------------------------
            1 -> {

                binding.information.setText(
                    R.string.result_human_wins
                )

                finishGame()
            }


            // ----------------------------------------------------
            // 2 = ganó O
            // ----------------------------------------------------
            2 -> {

                binding.information.setText(
                    R.string.result_computer_wins
                )

                finishGame()
            }


            // ----------------------------------------------------
            // 3 = empate
            // ----------------------------------------------------
            3 -> {

                binding.information.setText(
                    R.string.result_tie
                )

                finishGame()
            }
        }
    }


    // ============================================================
    // 9. FINALIZAR LA PARTIDA
    // ============================================================

    private fun finishGame() {

        // Marcamos la partida como terminada.
        gameOver = true


        // Deshabilitamos todas las casillas.
        //
        // Así solucionamos uno de los problemas
        // señalados expresamente en el reto:
        // no permitir movimientos después del final.
        for (button in boardButtons) {

            button.isEnabled = false
        }
    }


    // ============================================================
    // 10. LIBERAR VIEW BINDING
    // ============================================================

    override fun onDestroyView() {

        super.onDestroyView()

        // Evita conservar referencias a una vista
        // que ya fue destruida.
        _binding = null
    }
}