package com.daignosis.daignosis.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.daignosis.daignosis.R
import com.daignosis.daignosis.data.response.DataItem
import com.daignosis.daignosis.databinding.ActivityMainBinding
import com.daignosis.daignosis.ui.adapter.MainAdapter
import com.daignosis.daignosis.ui.article.ArticleActivity
import com.daignosis.daignosis.ui.consultation.HistoryActivity
import com.daignosis.daignosis.ui.login.LoginActivity
import com.daignosis.daignosis.ui.profile.ProfileActivity
import com.daignosis.daignosis.utils.Result
import com.daignosis.daignosis.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        mainViewModel = ViewModelProvider(
            this, ViewModelFactory(this)
        )[MainViewModel::class.java]
        checkToken()
        mainViewModel.rmvSession()


        mainViewModel.getArticle().second.observe(this){
            setRecycler(it)
        }
        mainViewModel.getArticle().first.observe(this){
            when (it) {
                is Result.Success -> binding.progressBar3.visibility = View.GONE
                is Result.Loading -> binding.progressBar3.visibility = View.VISIBLE
                is Result.Error -> Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }
        binding.btnOther.setOnClickListener {
            startActivity(Intent(this,ArticleActivity::class.java))
        }
        binding.btnConsult.setOnClickListener {
            checkTokenAI()
        }
    }

    private fun checkTokenAI() {
        mainViewModel.getToken().observe(this) {
            if (it.token.isEmpty()) {
                Toast.makeText(this, R.string.must_login, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            else {
                startActivity(Intent(this,HistoryActivity::class.java))
            }
        }
    }
    private fun checkToken() {
        mainViewModel.getToken().observe(this) {
            if (it.token.isEmpty()) {
                binding.btnLogin.visibility = View.VISIBLE
                binding.btnProfile.visibility = View.GONE
            } else {
                binding.btnLogin.visibility = View.GONE
                binding.btnProfile.visibility = View.VISIBLE
            }
        }
    }
    private fun setRecycler(article: List<DataItem>){
        binding.rvArtikelmain.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = MainAdapter(article)
        }
    }
}