package com.example.nb.ble_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btnConnect;
    Button btnDisconnect;
    EditText editText;
    EditText editMultilineText;

    private static final String BT_TAG = "bluetooth1";
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter btAdapter;

    int cnt = 0;

    // Создаем BroadcastReceiver для ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Когда найдено новое устройство
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Получаем объект BluetoothDevice из интента
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //Добавляем имя и адрес в array adapter, чтобы показвать в ListView
                editMultilineText.append(device.getName() + "\n" + device.getAddress() + "\n\n");
            }
        }
    };

    public MainActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnConnect = findViewById(R.id.btnConnect);
        btnDisconnect = findViewById(R.id.btnDisconnect);

        editText = findViewById(R.id.editText);
        editMultilineText = findViewById(R.id.editMultilineText);
        editMultilineText.setMovementMethod(new ScrollingMovementMethod());
        //editMultilineText.setInputType(InputType.TYPE_);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE не поддерживается"/*R.string.ble_not_supported*/, Toast.LENGTH_SHORT).show();
            finish();
        }

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        attachListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
    }

    private void attachListeners() {
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("Cnt " + cnt);
                cnt++;

            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMultilineText.append("new text\n");
                editMultilineText.scrollBy(0, View.FOCUS_DOWN);
            }
        });
    }


    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter == null) {
            //errorExit("Fatal Error", "Bluetooth не поддерживается");
            Log.d(BT_TAG, "Bluetooth не поддерживается");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(BT_TAG, "...Bluetooth включен...");


                btAdapter.startDiscovery();
                btScan();
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                Log.d(BT_TAG, "...запрос на включение Bluetooth...");
            }
        }
    }


    private void btScan() {
        // Регистрируем BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);// Не забудьте снять регистрацию в onDestroy
    }
}
