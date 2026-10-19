package com.example.smartkitchen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButtonToggleGroup

class MainActivity : AppCompatActivity() {

    private lateinit var headerBar: View
    private lateinit var simulationBar: View
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fragmentContainer: ViewGroup
    private lateinit var overlayContainer: ViewGroup

    private lateinit var toggleSimulation: MaterialButtonToggleGroup

    private var currentTabId: Int = R.id.nav_home
    private var isGasAlertState: Boolean = false

    // Inflated tab views
    private var homeView: View? = null
    private var monitoringView: View? = null
    private var historyView: View? = null
    private var settingsView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        headerBar = findViewById(R.id.header_bar)
        simulationBar = findViewById(R.id.simulation_bar)
        bottomNav = findViewById(R.id.bottom_navigation)
        fragmentContainer = findViewById(R.id.fragment_container)
        overlayContainer = findViewById(R.id.overlay_container)
        toggleSimulation = findViewById(R.id.toggle_simulation)

        // Setup Bottom Navigation Listener
        bottomNav.setOnItemSelectedListener { item ->
            switchTab(item.itemId)
            true
        }

        // Setup Simulation Toggle
        toggleSimulation.check(R.id.btn_sim_normal)
        toggleSimulation.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isGasAlertState = (checkedId == R.id.btn_sim_alert)
                updateStateAcrossViews()
            }
        }

        // Start with Splash Screen
        showSplashScreen()
    }

    private fun switchTab(tabId: Int) {
        currentTabId = tabId
        fragmentContainer.removeAllViews()

        val inflater = LayoutInflater.from(this)
        when (tabId) {
            R.id.nav_home -> {
                if (homeView == null) {
                    homeView = inflater.inflate(R.layout.fragment_home, fragmentContainer, false)
                    setupHomeView(homeView!!)
                }
                fragmentContainer.addView(homeView)
            }
            R.id.nav_monitoring -> {
                if (monitoringView == null) {
                    monitoringView = inflater.inflate(R.layout.fragment_monitoring, fragmentContainer, false)
                    setupMonitoringView(monitoringView!!)
                }
                fragmentContainer.addView(monitoringView)
            }
            R.id.nav_history -> {
                if (historyView == null) {
                    historyView = inflater.inflate(R.layout.fragment_history, fragmentContainer, false)
                    setupHistoryView(historyView!!)
                }
                fragmentContainer.addView(historyView)
            }
            R.id.nav_settings -> {
                if (settingsView == null) {
                    settingsView = inflater.inflate(R.layout.fragment_settings, fragmentContainer, false)
                    setupSettingsView(settingsView!!)
                }
                fragmentContainer.addView(settingsView)
            }
        }
        updateStateAcrossViews()
    }

    private fun updateStateAcrossViews() {
        // Update Home View
        homeView?.let { view ->
            val cardNormal = view.findViewById<View>(R.id.card_status_normal)
            val cardAlert = view.findViewById<View>(R.id.card_status_alert)
            val graphPreview = view.findViewById<GasGraphView>(R.id.graph_preview)
            val ivLed = view.findViewById<ImageView>(R.id.iv_led_icon)
            val tvLed = view.findViewById<TextView>(R.id.tv_led_status)
            val ivBuzzer = view.findViewById<ImageView>(R.id.iv_buzzer_icon)
            val tvBuzzer = view.findViewById<TextView>(R.id.tv_buzzer_status)

            if (isGasAlertState) {
                cardNormal.visibility = View.GONE
                cardAlert.visibility = View.VISIBLE
                graphPreview.isGasAlert = true

                ivLed.setColorFilter(ContextCompat.getColor(this, R.color.status_alert_red))
                tvLed.text = getString(R.string.led_red_on)

                ivBuzzer.setColorFilter(ContextCompat.getColor(this, R.color.status_alert_red))
                tvBuzzer.text = getString(R.string.buzzer_on)
            } else {
                cardNormal.visibility = View.VISIBLE
                cardAlert.visibility = View.GONE
                graphPreview.isGasAlert = false

                ivLed.setColorFilter(ContextCompat.getColor(this, R.color.status_normal_green))
                tvLed.text = getString(R.string.led_green_on)

                ivBuzzer.setColorFilter(ContextCompat.getColor(this, R.color.text_muted))
                tvBuzzer.text = getString(R.string.buzzer_off)
            }
        }

        // Update Monitoring View
        monitoringView?.let { view ->
            val graphDetailed = view.findViewById<GasGraphView>(R.id.graph_detailed)
            val tvVal = view.findViewById<TextView>(R.id.tv_monitoring_current_val)
            val ivStatusIcon = view.findViewById<ImageView>(R.id.iv_monitoring_status_icon)
            val tvStatusText = view.findViewById<TextView>(R.id.tv_monitoring_status_text)
            val tvAdc = view.findViewById<TextView>(R.id.tv_data_adc)
            val tvPpm = view.findViewById<TextView>(R.id.tv_data_ppm)

            if (isGasAlertState) {
                graphDetailed.isGasAlert = true
                tvVal.text = getString(R.string.val_1250)
                tvVal.setTextColor(ContextCompat.getColor(this, R.color.status_alert_red))
                ivStatusIcon.setImageResource(R.drawable.ic_warning_triangle)
                tvStatusText.text = getString(R.string.status_gas_alert)
                tvStatusText.setTextColor(ContextCompat.getColor(this, R.color.status_alert_red))
                tvAdc?.text = getString(R.string.val_sensor_1250)
                tvPpm?.text = "850 PPM"
            } else {
                graphDetailed.isGasAlert = false
                tvVal.text = getString(R.string.val_560)
                tvVal.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
                tvStatusText.text = getString(R.string.status_normal)
                tvStatusText.setTextColor(ContextCompat.getColor(this, R.color.status_normal_green))
                tvAdc?.text = getString(R.string.val_sensor_560)
                tvPpm?.text = getString(R.string.param_ppm_val)
            }
        }
    }

    private fun setupHomeView(view: View) {
        val btnEmergency = view.findViewById<Button>(R.id.btn_emergency_action)
        btnEmergency?.setOnClickListener {
            showEmergencyGuide()
        }
    }

    private fun setupMonitoringView(view: View) {
        val toggleMode = view.findViewById<MaterialButtonToggleGroup>(R.id.toggle_monitoring_mode)
        val containerGraph = view.findViewById<View>(R.id.container_graph_view)
        val containerData = view.findViewById<View>(R.id.container_data_view)

        toggleMode?.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.btn_tab_graph) {
                    containerGraph.visibility = View.VISIBLE
                    containerData.visibility = View.GONE
                } else {
                    containerGraph.visibility = View.GONE
                    containerData.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupHistoryView(view: View) {
        val itemAlert = view.findViewById<View>(R.id.item_history_alert)
        itemAlert?.setOnClickListener {
            showDetailHistory()
        }
    }

    private fun setupSettingsView(view: View) {
        val spinnerThreshold = view.findViewById<Spinner>(R.id.spinner_threshold)
        val spinnerInterval = view.findViewById<Spinner>(R.id.spinner_interval)
        val btnTestNotify = view.findViewById<Button>(R.id.btn_test_notification)
        val linkEmergency = view.findViewById<View>(R.id.btn_link_emergency)
        val linkAbout = view.findViewById<View>(R.id.btn_link_about)

        val thresholdOptions = arrayOf("500 ADC", "600 ADC", "700 ADC", "800 ADC", "900 ADC", "1000 ADC")
        val thresholdAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, thresholdOptions)
        thresholdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerThreshold?.adapter = thresholdAdapter
        spinnerThreshold?.setSelection(3) // 800 ADC default

        val intervalOptions = arrayOf("1 detik", "2 detik", "5 detik", "10 detik")
        val intervalAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, intervalOptions)
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInterval?.adapter = intervalAdapter
        spinnerInterval?.setSelection(1) // 2 detik default

        btnTestNotify?.setOnClickListener {
            Toast.makeText(this, "🔔 Uji Notifikasi: Sistem Smart Kitchen Aktif & Terhubung!", Toast.LENGTH_LONG).show()
        }

        linkEmergency?.setOnClickListener {
            showEmergencyGuide()
        }

        linkAbout?.setOnClickListener {
            showAboutScreen()
        }
    }

    // --- OVERLAY SCREENS NAVIGATION ---

    private fun showSplashScreen() {
        overlayContainer.removeAllViews()
        overlayContainer.visibility = View.VISIBLE
        val splashView = LayoutInflater.from(this).inflate(R.layout.fragment_splash, overlayContainer, false)

        val btnLoading = splashView.findViewById<Button>(R.id.btn_loading_splash)
        btnLoading.setOnClickListener {
            showWelcomeScreen()
        }

        splashView.setOnClickListener {
            showWelcomeScreen()
        }

        overlayContainer.addView(splashView)
    }

    private fun showWelcomeScreen() {
        overlayContainer.removeAllViews()
        overlayContainer.visibility = View.VISIBLE
        val welcomeView = LayoutInflater.from(this).inflate(R.layout.fragment_welcome, overlayContainer, false)

        val btnStart = welcomeView.findViewById<Button>(R.id.btn_welcome_start)
        val btnAbout = welcomeView.findViewById<Button>(R.id.btn_welcome_about)

        btnStart.setOnClickListener {
            overlayContainer.removeAllViews()
            overlayContainer.visibility = View.GONE
            switchTab(R.id.nav_home)
        }

        btnAbout.setOnClickListener {
            showAboutScreen()
        }

        overlayContainer.addView(welcomeView)
    }

    private fun showDetailHistory() {
        overlayContainer.removeAllViews()
        overlayContainer.visibility = View.VISIBLE
        val detailView = LayoutInflater.from(this).inflate(R.layout.fragment_history_detail, overlayContainer, false)

        val btnBack = detailView.findViewById<ImageView>(R.id.btn_back_detail)
        val btnEmergency = detailView.findViewById<Button>(R.id.btn_detail_emergency_action)

        btnBack.setOnClickListener {
            overlayContainer.removeAllViews()
            overlayContainer.visibility = View.GONE
        }

        btnEmergency.setOnClickListener {
            showEmergencyGuide()
        }

        overlayContainer.addView(detailView)
    }

    private fun showEmergencyGuide() {
        overlayContainer.removeAllViews()
        overlayContainer.visibility = View.VISIBLE
        val guideView = LayoutInflater.from(this).inflate(R.layout.fragment_emergency_guide, overlayContainer, false)

        val btnBack = guideView.findViewById<ImageView>(R.id.btn_back_emergency)
        btnBack.setOnClickListener {
            overlayContainer.removeAllViews()
            overlayContainer.visibility = View.GONE
        }

        overlayContainer.addView(guideView)
    }

    private fun showAboutScreen() {
        overlayContainer.removeAllViews()
        overlayContainer.visibility = View.VISIBLE
        val aboutView = LayoutInflater.from(this).inflate(R.layout.fragment_about, overlayContainer, false)

        val btnBack = aboutView.findViewById<ImageView>(R.id.btn_back_about)
        btnBack.setOnClickListener {
            overlayContainer.removeAllViews()
            overlayContainer.visibility = View.GONE
        }

        overlayContainer.addView(aboutView)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (overlayContainer.isVisible && overlayContainer.childCount > 0) {
            overlayContainer.removeAllViews()
            overlayContainer.visibility = View.GONE
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}