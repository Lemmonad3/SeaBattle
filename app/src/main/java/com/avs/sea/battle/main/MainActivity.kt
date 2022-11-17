package com.avs.sea.battle.main

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.avs.sea.battle.*
import com.avs.sea.battle.databinding.ActivityMainBinding
import com.google.android.play.core.review.ReviewManagerFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val customOnTouchListener = View.OnTouchListener(implementCustomTouchListener())

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.mainViewModel = viewModel

        binding.viewComputer.provideViewModel(viewModel)

        viewModel.status.observe(this) { newStatusId ->
            binding.tvStatus.text = resources.getText(newStatusId)
        }

        viewModel.selectedByPersonCoordinate.observe(this) { point ->
            binding.viewComputer.getSelectedCoordinate(point)
            binding.viewFire.visibility = if (point == null) View.INVISIBLE else View.VISIBLE
        }

        viewModel.selectedByComputerCoordinate.observe(this) {
            binding.progressBar.visibility = View.VISIBLE
        }

        viewModel.personShips.observe(this) { coordinates ->
            binding.viewPerson.getShipsCoordinates(coordinates)
            if (coordinates.isNotEmpty()) {
                binding.viewStart.visibility = View.VISIBLE
            }
        }

        viewModel.computerShips.observe(this) { coordinates ->
            binding.viewComputer.setShipsCoordinates(coordinates)
        }

        viewModel.personSuccessfulShots.observe(this) { coordinates ->
            binding.viewComputer.getCrossesCoordinates(coordinates)
        }

        viewModel.personFailedShots.observe(this) { coordinates ->
            binding.viewComputer.getDotsCoordinates(coordinates)
        }

        viewModel.computerSuccessfulShots.observe(this) { coordinates ->
            binding.viewPerson.getCrossesCoordinates(coordinates)
            binding.progressBar.visibility = View.INVISIBLE
        }

        viewModel.computerFailedShots.observe(this) { coordinates ->
            binding.viewPerson.getDotsCoordinates(coordinates)
            binding.progressBar.visibility = View.INVISIBLE
        }

        viewModel.startGameEvent.observe(this) { isStarted ->
            if (isStarted) binding.viewStart.visibility = View.GONE
            if (!isStarted) binding.viewNewGame.visibility = View.INVISIBLE
            binding.viewGenerate.visibility = if (isStarted) View.INVISIBLE else View.VISIBLE
        }

        viewModel.endGameEvent.observe(this) { eventPair ->
            if (eventPair.first) {
                binding.viewNewGame.visibility = View.VISIBLE
                if (eventPair.second == Player.PERSON) {
                    launchReviewFlow()
                }
            } else {
                binding.viewNewGame.visibility = View.INVISIBLE
            }
        }

        binding.viewGenerate.setOnTouchListener(customOnTouchListener)
        binding.viewFire.setOnTouchListener(customOnTouchListener)
        binding.viewStart.setOnTouchListener(customOnTouchListener)
        binding.viewNewGame.setOnTouchListener(customOnTouchListener)
    }

    private fun implementCustomTouchListener(): (View, MotionEvent) -> Boolean {
        return { v: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                    setTextColor(v, R.color.colorPrimary)
                }
                MotionEvent.ACTION_UP -> {
                    v.background = ContextCompat.getDrawable(this, R.drawable.square_background)
                    setTextColor(v, R.color.colorPrimaryDark)
                    v.performClick()
                }
            }
            true
        }
    }

    private fun setTextColor(v: View, color: Int) {
        if (v is TextView) v.setTextColor(
            ContextCompat.getColor(this, color)
        )
    }



    private fun launchReviewFlow() {
        baseContext?.let { context ->
            val manager = ReviewManagerFactory.create(context)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener {
                val flow = manager.launchReviewFlow(this, it.result)
                flow.addOnCompleteListener {
                    Log.d("Review", "Review flow completed")
                }
            }
        }
    }
}
