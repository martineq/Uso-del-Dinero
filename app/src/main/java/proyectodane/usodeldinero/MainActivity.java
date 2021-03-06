package proyectodane.usodeldinero;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Agrego "Splash Screen"
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        // Verifico inicialización de archivo de valores (Billetera)
        WalletManager.getInstance().checkFirstRun(this);

        // Inicio pantalla principal con tabs
        Intent intent = new Intent(this, MainTabActivity.class);
        startActivity(intent);

    }

}
