package co.edu.unal.tictactoe

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import co.edu.unal.tictactoe.databinding.FragmentFirstBinding

/**
 * FirstFragment controla la interfaz del juego.
 *
 * Reto 5:
 * - El tablero utiliza una vista personalizada BoardView.
 * - El tablero, las X y las O se dibujan mediante Canvas.
 * - Se detectan los toques directamente sobre BoardView.
 * - Se reproducen sonidos diferentes para el jugador y Android.
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

    private val game = TicTacToeGame()

    private var gameOver = false

    // Evita que el jugador toque otra casilla
    // mientras Android está realizando su movimiento.
    private var computerThinking = false


    // ============================================================
    // 3. SONIDO
    // ============================================================
// Indica si los efectos de sonido están activados.
// Por defecto comienzan activados.
    private var soundEnabled = true

    // Utilizamos un solo MediaPlayer para evitar
// que los sonidos se reproduzcan simultáneamente.
    private var mediaPlayer: MediaPlayer? = null



    // ============================================================
    // 4. CREACIÓN DE LA INTERFAZ
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
    // 5. CUANDO LA VISTA YA ESTÁ CREADA
    // ============================================================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        // --------------------------------------------------------
        // DETECTAR TOQUES SOBRE BOARDVIEW
        // --------------------------------------------------------

        binding.boardView.onCellClickListener = { position ->

            humanMove(position)
        }


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

                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {

                    menuInflater.inflate(
                        R.menu.options_menu,
                        menu
                    )
                }


                override fun onMenuItemSelected(
                    menuItem: MenuItem
                ): Boolean {

                    return when (menuItem.itemId) {

                        // NEW GAME
                        R.id.new_game -> {

                            startNewGame()

                            true
                        }


                        // DIFFICULTY
                        R.id.ai_difficulty -> {

                            showDifficultyDialog()

                            true
                        }
// SOUND
                        R.id.sound -> {

                            soundEnabled = !soundEnabled

                            menuItem.isChecked = soundEnabled

                            menuItem.title =
                                if (soundEnabled) {
                                    "Sound: ON"
                                } else {
                                    "Sound: OFF"
                                }

                            true
                        }

                        // QUIT
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
    // 6. INICIAR UNA NUEVA PARTIDA
    // ============================================================

    private fun startNewGame() {

        // Detenemos cualquier sonido anterior.
        stopSound()

        // Reiniciamos la lógica del juego.
        game.resetGame()

        // Limpiamos visualmente el BoardView.
        binding.boardView.clearBoard()

        gameOver = false
        computerThinking = false

        binding.information.setText(
            R.string.first_human
        )
    }


    // ============================================================
    // 7. MOVIMIENTO DEL JUGADOR HUMANO
    // ============================================================

    private fun humanMove(position: Int) {

        // Si la partida terminó, no permitimos más jugadas.
        if (gameOver) {
            return
        }

        // Si Android está pensando, tampoco permitimos otra jugada.
        if (computerThinking) {
            return
        }

        // Evitamos seleccionar una casilla ya ocupada.
        if (binding.boardView.getCell(position) != ' ') {
            return
        }

        // Intentamos realizar la jugada en la lógica.
        val validMove = game.makeMove(position)

        if (!validMove) {
            return
        }


        // Dibujamos la X en BoardView.
        binding.boardView.setCell(
            position,
            'X'
        )


        // --------------------------------------------------------
        // SONIDO DEL MOVIMIENTO DEL JUGADOR
        // --------------------------------------------------------

        playSound(R.raw.human_move)


        // Revisamos si el jugador ganó.
        val result = game.checkWinner()


        // Si el jugador ganó o hubo empate,
        // actualizamos inmediatamente el estado.
        if (result != 0) {

            updateGameStatus(result)

            return
        }


        // --------------------------------------------------------
        // TURNO DE ANDROID
        // --------------------------------------------------------

        computerThinking = true

        binding.information.setText(
            R.string.turn_computer
        )


        // Pequeña pausa para separar visualmente y acústicamente
        // la jugada humana de la jugada de Android.
        binding.boardView.postDelayed({

            if (_binding == null || gameOver) {
                computerThinking = false
                return@postDelayed
            }

            computerMove()

            val computerResult = game.checkWinner()

            computerThinking = false

            updateGameStatus(computerResult)

        }, 1000)
    }


    // ============================================================
    // 8. MOVIMIENTO DEL COMPUTADOR
    // ============================================================

    private fun computerMove() {

        // La lógica selecciona una posición dependiendo
        // de Easy, Harder o Expert.
        val position = game.getComputerMove()


        // No quedan movimientos.
        if (position == -1) {
            return
        }


        // Registramos el movimiento del computador.
        game.makeMove(position)


        // Dibujamos O sobre el Canvas.
        binding.boardView.setCell(
            position,
            'O'
        )


        // --------------------------------------------------------
        // SONIDO DEL MOVIMIENTO DEL COMPUTADOR
        // --------------------------------------------------------

        playSound(R.raw.computer_move)
    }


    // ============================================================
    // 9. ACTUALIZAR EL ESTADO DE LA PARTIDA
    // ============================================================

    private fun updateGameStatus(result: Int) {

        when (result) {

            // La partida continúa.
            0 -> {

                binding.information.setText(
                    R.string.turn_human
                )
            }


            // Ganó el jugador.
            1 -> {

                binding.information.setText(
                    R.string.result_human_wins
                )

                finishGame()
            }


            // Ganó Android.
            2 -> {

                binding.information.setText(
                    R.string.result_computer_wins
                )

                finishGame()
            }


            // Empate.
            3 -> {

                binding.information.setText(
                    R.string.result_tie
                )

                finishGame()
            }
        }
    }


    // ============================================================
    // 10. FINALIZAR PARTIDA
    // ============================================================

    private fun finishGame() {

        gameOver = true
        computerThinking = false
    }


    // ============================================================
    // 11. SELECCIONAR DIFICULTAD
    // ============================================================

    private fun showDifficultyDialog() {

        val levels = arrayOf(
            "Easy",
            "Harder",
            "Expert"
        )


        val selected = when (game.getDifficultyLevel()) {

            TicTacToeGame.DifficultyLevel.EASY -> 0

            TicTacToeGame.DifficultyLevel.HARDER -> 1

            TicTacToeGame.DifficultyLevel.EXPERT -> 2
        }


        AlertDialog.Builder(requireContext())

            .setTitle("Choose difficulty")

            .setSingleChoiceItems(
                levels,
                selected
            ) { dialog, which ->


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


                dialog.dismiss()
            }

            .show()
    }


    // ============================================================
    // 12. CONFIRMAR SALIDA
    // ============================================================

    private fun showQuitDialog() {

        AlertDialog.Builder(requireContext())

            .setTitle("Quit")

            .setMessage(
                "Are you sure you want to quit?"
            )

            .setPositiveButton("Yes") { _, _ ->

                requireActivity().finish()
            }

            .setNegativeButton(
                "No",
                null
            )

            .show()
    }


    // ============================================================
    // 13. REPRODUCIR EFECTOS DE SONIDO
    // ============================================================

    /**
     * Reproduce un efecto de sonido almacenado en res/raw.
     *
     * Antes de reproducir un sonido nuevo se libera el anterior.
     * De esta manera evitamos que los efectos se superpongan.
     */
    private fun playSound(soundResource: Int) {

        // Si el usuario desactivó el sonido,
        // no reproducimos ningún efecto.
        if (!soundEnabled) {
            return
        }

        // Detenemos y liberamos cualquier sonido anterior.
        stopSound()

        // Creamos el nuevo efecto de sonido.
        mediaPlayer = MediaPlayer.create(
            requireContext(),
            soundResource
        )

        // Cuando termina el sonido liberamos MediaPlayer.
        mediaPlayer?.setOnCompletionListener {

            it.release()

            mediaPlayer = null
        }

        // Reproducimos el sonido.
        mediaPlayer?.start()
    }


    /**
     * Detiene y libera el sonido que se esté reproduciendo.
     */
    private fun stopSound() {

        mediaPlayer?.let {

            if (it.isPlaying) {
                it.stop()
            }

            it.release()
        }

        mediaPlayer = null
    }


    // ============================================================
    // 14. LIBERAR RECURSOS
    // ============================================================

    override fun onDestroyView() {

        // Detenemos cualquier sonido activo.
        stopSound()

        // Cancelamos movimientos pendientes de Android.
        if (_binding != null) {
            binding.boardView.removeCallbacks(null)
        }

        super.onDestroyView()

        _binding = null
    }
}