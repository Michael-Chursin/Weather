package com.example.weather

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weather.ui.theme.WeatherTheme
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.util.Date


class MainActivity : ComponentActivity() {
    var city = mutableStateOf("Tomsk")
    var units = mutableStateOf("metric")
    var testUrl = "https://api.openweathermap.org/data/2.5/weather?q=${city.value}&units=${units.value}&lang=ru&appid=6de0f0fcb5b2951a093ce5729d9b1792"
    lateinit var mRequestQueue:RequestQueue
    lateinit var themeSwitch: Switch
    lateinit var cityEditText: EditText
    lateinit var tempTextView:TextView
    lateinit var windTextView:TextView
    lateinit var humidityTextView: TextView
    lateinit var weatherTextView: TextView
    lateinit var sunTextView: TextView
    lateinit var callbutton: Button
    lateinit var metricCheckBox: CheckBox
    lateinit var imperialCheckBox: CheckBox
    lateinit var whereTextView: TextView
    var temp = 0.0
    var feelslike = 0.0
    var windSpeed = 0.0
    var humidity = 0
    var windDirInt = 0
    var windDirStr = ""
    var mainWeather = ""
    var weatherDesc = ""
    var sunrise = ""
    var sunset = ""
    var country = ""
    private fun getWeather(url: String) {
        val request = JsonObjectRequest(
            Request.Method.GET,
            url, null, { response ->
                try {
                    val weather = response.getJSONArray("weather")
                    val main = response.getJSONObject("main")
                    val wind = response.getJSONObject("wind")
                    val sys = response.getJSONObject("sys")
                    val timezone = response.getInt("timezone")
                    temp = main.getDouble("temp")
                    feelslike = main.getDouble("feels_like")
                    windSpeed = wind.getDouble("speed")
                    windDirInt = wind.getInt("deg")
                    humidity = main.getInt("humidity")
                    val srunix = sys.getInt("sunrise") + timezone
                    val ssunix = sys.getInt("sunset") + timezone
                    var date = Instant.ofEpochSecond((srunix).toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime()
                    sunrise = "${date.hour}:${date.minute}:${date.second}"
                    date = Instant.ofEpochSecond((ssunix).toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime()
                    sunset = "${date.hour}:${date.minute}:${date.second}"
                    country = sys.getString("country")
                    for (i in 0..< weather.length()){
                        val tempw = weather.getJSONObject(i)
                        mainWeather = mainWeather + tempw.getString("main") + ", "
                        weatherDesc = weatherDesc + tempw.getString("description") + ", "
                    }
                    if ((windDirInt in 0..15) || (windDirInt in 345..360)){
                        windDirStr = "Север"
                    }
                    else if (windDirInt in 16..74){
                        windDirStr = "Северо-Восток"
                    }
                    else if (windDirInt in 75..105){
                        windDirStr = "Восток"
                    }
                    else if (windDirInt in 106..164){
                        windDirStr = "Юго-восток"
                    }
                    else if (windDirInt in 165..195){
                        windDirStr = "Юг"
                    }
                    else if (windDirInt in 196..254){
                        windDirStr = "Юго-запад"
                    }
                    else if (windDirInt in 255..285){
                        windDirStr = "Запад"
                    }
                    else if (windDirInt in 286..344){
                        windDirStr = "Северо-запад"
                    }
                    setValues()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request)
    }
    private fun setValues() {
        var tempUnit = ""
        var speedUnit = ""
        if(metricCheckBox.isChecked){
            tempUnit = "C"
            speedUnit = "м/с"
        }
        else{
            tempUnit = "F"
            speedUnit = "миль/час"
        }
        cityEditText.setText(city.value)
        tempTextView.text = "Температура: $temp $tempUnit\nОщущается: $feelslike $tempUnit"
        windTextView.text = "Скорость ветра: $windSpeed $speedUnit\nНаправление: $windDirStr"
        humidityTextView.text = "Влажность: $humidity%"
        weatherTextView.text = "Погода: $mainWeather\nОписание: $weatherDesc"
        sunTextView.text = "Восход: $sunrise\nЗакат: $sunset"
        callbutton.setOnClickListener {
            city.value = cityEditText.text.toString();
            if(metricCheckBox.isChecked){
                units.value = "metric"
            }
            else{
                units.value = "imperial"
            }
            testUrl = "https://api.openweathermap.org/data/2.5/weather?q=${city.value}&units=${units.value}&lang=ru&appid=6de0f0fcb5b2951a093ce5729d9b1792";
            getWeather(testUrl);
            setValues(); }
        whereTextView.text = "кстати, а где это?\n$country"
        temp = 0.0
        feelslike = 0.0
        windSpeed = 0.0
        humidity = 0
        windDirInt = 0
        windDirStr = ""
        mainWeather = ""
        weatherDesc = ""
        sunrise = ""
        sunset = ""
        country = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                window.setBackgroundDrawable(getDrawable(R.color.black))
                themeSwitch.background = getDrawable(R.color.black)
                themeSwitch.setTextColor(Color.White.toArgb())
                themeSwitch.text = "Ночная тема"
                cityEditText.setBackgroundDrawable(getDrawable(R.drawable.ic_edittext_nightlayout))
                cityEditText.setTextColor(Color.White.toArgb())
                tempTextView.background = getDrawable(R.color.black)
                tempTextView.setTextColor(Color.White.toArgb())
                windTextView.background = getDrawable(R.color.black)
                windTextView.setTextColor(Color.White.toArgb())
                humidityTextView.background = getDrawable(R.color.black)
                humidityTextView.setTextColor(Color.White.toArgb())
                weatherTextView.background = getDrawable(R.color.black)
                weatherTextView.setTextColor(Color.White.toArgb())
                sunTextView.background = getDrawable(R.color.black)
                sunTextView.setTextColor(Color.White.toArgb())
                metricCheckBox.background = getDrawable(R.color.black)
                metricCheckBox.setTextColor(Color.White.toArgb())
                imperialCheckBox.background = getDrawable(R.color.black)
                imperialCheckBox.setTextColor(Color.White.toArgb())
                callbutton.background = getDrawable(R.color.teal_700)
                callbutton.setTextColor(Color.White.toArgb())
                whereTextView.background = getDrawable(R.color.black)
                whereTextView.setTextColor(Color.White.toArgb())
            }
            else{
                window.setBackgroundDrawable(getDrawable(R.color.white))
                themeSwitch.background = getDrawable(R.color.white)
                themeSwitch.setTextColor(Color.Black.toArgb())
                themeSwitch.text = "Дневная тема"
                cityEditText.setBackgroundDrawable(getDrawable(R.drawable.ic_edittext_daylayout))
                cityEditText.setTextColor(Color.Black.toArgb())
                tempTextView.background = getDrawable(R.color.white)
                tempTextView.setTextColor(Color.Black.toArgb())
                windTextView.background = getDrawable(R.color.white)
                windTextView.setTextColor(Color.Black.toArgb())
                humidityTextView.background = getDrawable(R.color.white)
                humidityTextView.setTextColor(Color.Black.toArgb())
                weatherTextView.background = getDrawable(R.color.white)
                weatherTextView.setTextColor(Color.Black.toArgb())
                sunTextView.background = getDrawable(R.color.white)
                sunTextView.setTextColor(Color.Black.toArgb())
                metricCheckBox.background = getDrawable(R.color.white)
                metricCheckBox.setTextColor(Color.Black.toArgb())
                imperialCheckBox.background = getDrawable(R.color.white)
                imperialCheckBox.setTextColor(Color.Black.toArgb())
                callbutton.background = getDrawable(R.color.purple_200)
                callbutton.setTextColor(Color.Black.toArgb())
                whereTextView.background = getDrawable(R.color.white)
                whereTextView.setTextColor(Color.Black.toArgb())
            }
        }
        cityEditText = findViewById(R.id.cityEditText);
        tempTextView = findViewById(R.id.tempTextView);
        windTextView = findViewById(R.id.windTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        weatherTextView = findViewById(R.id.weatherTextView);
        sunTextView = findViewById(R.id.sunTextView);
        metricCheckBox = findViewById(R.id.metricCheckbox);
        metricCheckBox.isChecked = true;
        imperialCheckBox = findViewById(R.id.imperialCheckbox);
        imperialCheckBox.isChecked = false;
        metricCheckBox.setOnCheckedChangeListener { _, isChecked ->
            imperialCheckBox.isChecked = isChecked != true
        }
        imperialCheckBox.setOnCheckedChangeListener { _, isChecked ->
            metricCheckBox.isChecked = isChecked != true
        }
        callbutton = findViewById(R.id.callButton);
        whereTextView = findViewById(R.id.whereTextView);
        mRequestQueue = Volley.newRequestQueue(this);
        getWeather(testUrl);
        setValues();
    }
}

