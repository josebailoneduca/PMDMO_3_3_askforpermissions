package com.imagenprogramada.askforpermission;

import static android.app.appsearch.SetSchemaRequest.READ_CONTACTS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.imagenprogramada.askforpermission.databinding.ActivityMainBinding;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        //al pulsar el boton se comprueban permisos
       binding.btnCargarContacto.setOnClickListener(view -> comprobarPermiso());
        View view = binding.getRoot();
        setContentView(view);
    }


    /**
     * Comprobar el permiso de acceso a los contactos:
     * Si no se tiene se pide(la respuesta será manejada por onRequestPermissionsResult)
     * Si ya se tiene se pasa a cargar un contacto
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private void comprobarPermiso() {

        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_CONTACTS"}, 1);
        } else {
            cargarContacto();
        }
    }

    /**
     * Maneja la respuesta de haber pedido permisos de acceso a los contactos:
     * -Si se han garantizado se carga un contacto.
     * -Si no se ha garantizado pero no se ha marcado la casilla de "no preguntar otra vez" se le explica que es necesario
     * -Si ha marcado la casilla de "no preguntar otra vez" se le explica que el permiso es necesario y que puede
     *  activarlo manualmente en la configuración del teléfono
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0){
            //si el permiso ha sido concedido
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cargarContacto();
            }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                //si no ha sido concedido entonces:
                // Si aún no ha marcado el no ser preguntado más se le da al usuario
                // una explicacion de la necesidad de dar el permiso
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_CONTACTS")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.msg_explicacion).setTitle(R.string.aviso);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    //Si ha marcado que no quiere volver a ser preguntado se le explica que es imprescindible
                    //y que si quiere usar la aplicacion debe aceptarlo manualmente en la configuracion del telefono
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.msg_explicacion_manual).setTitle(R.string.aviso);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }
    }


    /**
     * Cargar un contacto del teléfono y mostrarlo en pantalla
     */
    private void cargarContacto() {

        ContentResolver contentResolver=getContentResolver();
        Cursor cursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        ArrayList<String> lista = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                lista.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

            }while (cursor.moveToNext());

            if (lista.size()>0) {
                Random r = new Random();
                binding.tvNombre.setText(lista.get(r.nextInt(lista.size())));
            }else{
                binding.tvNombre.setText("No tiene contactos en el telefono");
            }
        }else{
            binding.tvNombre.setText("No tiene contactos en el telefono");
        }
    }
}