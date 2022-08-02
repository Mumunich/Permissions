package com.bignerdranch.android.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bignerdranch.android.permissions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Проверяем наличие разрешения
        binding.requestLocationButton.setOnClickListener {
            if( ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                onLocationPermissionGranted()
            } else{
                // Если разрешение отсутствует,то запрашиваем его
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),
                    RQ_PERMISSIONS_FOR_FEATURES_1_CODE
                )
            }
        }

        // Можно и не проверять наличие разрешения,если у приложения будет разрешение,то сразу вызовется onRequestPermissionsResult
        binding.requestCameraAndRecordButton.setOnClickListener {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    RQ_PERMISSIONS_FOR_FEATURES_2_CODE
                )
        }
    }

    // Разные кнопки,запрашивают разные разрешения,так что у них будут разные requestCode
    // 1 аргумент отвечает за то какой именно запрос вернулся,2 и 3 это результаты,2 массив разрешений,3 массив результатов(дано разрешение или нет)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            RQ_PERMISSIONS_FOR_FEATURES_1_CODE -> {
                // grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                // можно заменить на grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if(grantResults.all { it == PackageManager.PERMISSION_GRANTED }){
                    onLocationPermissionGranted()
                } else{
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
                        askUserForOpeningAppSettings()
                    } else {
                        Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            RQ_PERMISSIONS_FOR_FEATURES_2_CODE -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    onCameraAndRecordPermissionGranted()
                } else {
                    // Если пользователь не дал разрешение и решил не показывать диалог
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                        askUserForOpeningAppSettings()
                    } else {
                        Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun askUserForOpeningAppSettings() {
        // Создаём интент на запуск системной активтити,которая будет показывать системные настройки нашего приложения (за это отвечает 1 строка)
        // именно нашего приложения (за это отвечает 2 строка),вторым параметром передаём ссылку на наше приложение
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package",packageName,null)
        )
        // Проверяем что интент можно запустить,ибо мало ли на какой сборке и на каком телефоне запускается приложение
        if(packageManager.resolveActivity(appSettingsIntent,PackageManager.MATCH_DEFAULT_ONLY) == null){
            Toast.makeText(this,"Permissions are denied forever",Toast.LENGTH_SHORT).show()
        } else{
            AlertDialog.Builder(this)
                .setTitle("Permissions denied")
                .setMessage("You have denied permissions forever." + "You can change your decision in app settings.\n\n" + "Would you like to open app settings?")
                .setPositiveButton("Open"){ _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }

    private fun onLocationPermissionGranted(){
        Toast.makeText(this,"Location permission is granted",Toast.LENGTH_SHORT).show()
    }
    private fun onCameraAndRecordPermissionGranted(){
        Toast.makeText(this,"Camera permissions is granted",Toast.LENGTH_SHORT).show()
    }

    private companion object{
        const val RQ_PERMISSIONS_FOR_FEATURES_1_CODE = 1
        const val RQ_PERMISSIONS_FOR_FEATURES_2_CODE = 2
    }
}