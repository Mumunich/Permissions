package com.bignerdranch.android.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bignerdranch.android.permissions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val feature1PermissionsRequestLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionsResultForFeatures1
        )

    private val feature2PermissionsRequestLauncher = registerForActivityResult(
        RequestPermission(),
        ::onGotPermissionsResultForFeatures2
        )


    // Что бы использовать Activity Result Api
    // нужно создать лаунчер,вызвать его,передать ему нужные разрешения и при создании лаунчера,определить логику обработки результатов
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Проверяем наличие разрешения
        binding.requestLocationButton.setOnClickListener {
            feature1PermissionsRequestLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION))
        }

        // Можно и не проверять наличие разрешения,если у приложения будет разрешение,то сразу вызовется onRequestPermissionsResult
        binding.requestCameraAndRecordButton.setOnClickListener {
            feature2PermissionsRequestLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    private fun onGotPermissionsResultForFeatures1(grantResults:Map<String,Boolean>){
        // Если все значения внутри этой мапы равны тру,то показываем сообщение
        if(grantResults.entries.all { it.value  }){
            onLocationPermissionGranted()
        } else{
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
                askUserForOpeningAppSettings()
            } else {
                Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onGotPermissionsResultForFeatures2(granted:Boolean){
        if(granted){
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

    // Context это мост к возможностям операционной системы андроид и это источник инфы об окружении в котором выполняется приложение
    // это глаза и уши,он позволяет видеть всё андроид специфическое и получать инфу о том где  и как выполняется приложение
    // а так же руки и ноги,он позволяет взаимодействовать со всеми андроид специфическими функциями(показ уведомлений,управление адаптерами и тд)
    // активити наследуется от контекста,поэтому она сама по себе является контекстом,сервисы и application тоже имеют контекст
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