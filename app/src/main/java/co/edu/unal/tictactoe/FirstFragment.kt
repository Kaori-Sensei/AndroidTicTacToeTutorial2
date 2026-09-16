package co.edu.unal.tictactoe

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import co.edu.unal.tictactoe.databinding.FragmentFirstBinding


/**
 * FirstFragment controla la parte visual del juego.
 *
 * Aquí conectamos:
 * - Los nueve botones del tablero.
 * - El texto que informa el estado de la partida.
 * - El botón "New Game".
 * - El menú de opciones.
 *
 * La lógica del juego permanece separada
 * en la clase TicTacToeGame.
 */
class FirstFragment : Fragment() {


    // ============================================================
    // 1. VIEW BINDING
    // ============================================================

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!


    // ============================================================
    // 2. LÓGICA DEL JUEGO
    // ============================================================

    // Instancia del "cerebro" del Tic-Tac-Toe.
    private val game = TicTacToeGame()

    // Array con los nueve botones del tablero.
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


        // --------------------------------------------------------
        // MENÚ DE OPCIONES
        // --------------------------------------------------------

        requireActivity().addMenuProvider(

            object : MenuProvider {

                // Creamos el menú utilizando options_menu.xml.
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {

                    menuInflater.inflate(
                        R.menu.options_menu,
                        menu
                    )
                }


                // Detectamos qué opción seleccionó el usuario.
                override fun onMenuItemSelected(
                    menuItem: MenuItem
                ): Boolean {

                    return when (menuItem.itemId) {


                        // ----------------------------------------
                        // NEW GAME
                        // ----------------------------------------

                        R.id.new_game -> {

                            startNewGame()

                            true
                        }


                        // ----------------------------------------
                        // DIFFICULTY
                        // ----------------------------------------

                        R.id.ai_difficulty -> {

                            showDifficultyDialog()

                            true
                        }


                        // ----------------------------------------
                        // QUIT
                        // ----------------------------------------

                        R.id.quit -> {

                            showQuitDialog()

                            true
                        }


                        else -> false
                    }
                }
            },

            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
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
        val validMove = game.makeMove(position)


        // Si la posición ya estaba ocupada, no hacemos nada.
        if (!validMove) {
            return
        }


        // Mostramos X.
        boardButtons[position].text = "X"

        // X aparece verde.
        boardButtons[position].setTextColor(
            Color.rgb(0, 180, 0)
        )

        // Deshabilitamos esa posición.
        boardButtons[position].isEnabled = false


        // Revisamos si X ganó.
        var result = game.checkWinner()


        // Si la partida continúa,
        // dejamos jugar al computador.
        if (result == 0) {

            computerMove()

            // Revisamos nuevamente después
            // del movimiento del computador.
            result = game.checkWinner()
        }


        // Actualizamos el estado de la partida.
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


        // TicTacToeGame decide la posición según
        // el nivel de dificultad seleccionado.
        val position = game.getComputerMove()


        // -1 significa que no existen posiciones disponibles.
        if (position == -1) {
            return
        }


        // currentPlayer es O porque makeMove()
        // cambió el turno después de jugar X.
        game.makeMove(position)


        // Mostramos O.
        boardButtons[position].text = "O"

        // O aparece rojo.
        boardButtons[position].setTextColor(
            Color.rgb(200, 0, 0)
        )

        // Deshabilitamos la posición.
        boardButtons[position].isEnabled = false
    }


    // ============================================================
    // 8. ACTUALIZAR EL RESULTADO
    // ============================================================

    private fun updateGameStatus(result: Int) {

        when (result) {


            // ----------------------------------------------------
            // 0 = LA PARTIDA CONTINÚA
            // ----------------------------------------------------

            0 -> {

                binding.information.setText(
                    R.string.turn_human
                )
            }


            // ----------------------------------------------------
            // 1 = GANÓ X
            // ----------------------------------------------------

            1 -> {

                binding.information.setText(
                    R.string.result_human_wins
                )

                finishGame()
            }


            // ----------------------------------------------------
            // 2 = GANÓ O
            // ----------------------------------------------------

            2 -> {

                binding.information.setText(
                    R.string.result_computer_wins
                )

                finishGame()
            }


            // ----------------------------------------------------
            // 3 = EMPATE
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


        // Deshabilitamos todas las casillas para impedir
        // movimientos después de terminar la partida.
        for (button in boardButtons) {

            button.isEnabled = false
        }
    }


    // ============================================================
    // 10. SELECCIONAR DIFICULTAD
    // ============================================================

    private fun showDifficultyDialog() {

        // Niveles disponibles.
        val levels = arrayOf(
            "Easy",
            "Harder",
            "Expert"
        )


        // Identificamos cuál dificultad está
        // seleccionada actualmente.
        val selected = when (game.getDifficultyLevel()) {

            TicTacToeGame.DifficultyLevel.EASY -> 0

            TicTacToeGame.DifficultyLevel.HARDER -> 1

            TicTacToeGame.DifficultyLevel.EXPERT -> 2
        }


        // Creamos el cuadro de diálogo.
        AlertDialog.Builder(requireContext())

            .setTitle("Choose difficulty")

            .setSingleChoiceItems(
                levels,
                selected
            ) { dialog, which ->


                // Cambiamos la dificultad según
                // la opción seleccionada.
                when (which) {

                    0 -> game.setDifficultyLevel(
                        TicTacToeGame.DifficultyLevel.EASY
                    )

                    1 -> game.setDifficultyLevel(
                        TicTacToeGame.DifficultyLevel.HARDER
                    )

                    2 -> game.setDifficultyLevel(
                        TicTacToeGame.DifficultyLevel.EXPERT
                    )
                }


                // Cerramos el cuadro de diálogo.
                dialog.dismiss()
            }

            .show()
    }


    // ============================================================
    // 11. CONFIRMAR SALIDA
    // ============================================================

    private fun showQuitDialog() {

        AlertDialog.Builder(requireContext())

            .setTitle("Quit")

            .setMessage(
                "Are you sure you want to quit?"
            )

            .setPositiveButton("Yes") { _, _ ->

                // Cerramos la Activity.
                requireActivity().finish()
            }

            .setNegativeButton(
                "No",
                null
            )

            .show()
    }


    // ============================================================
    // 12. LIBERAR VIEW BINDING
    // ============================================================

    override fun onDestroyView() {

        super.onDestroyView()

        // Evita conservar referencias a una vista
        // que ya fue destruida.
        _binding = null
    }
}