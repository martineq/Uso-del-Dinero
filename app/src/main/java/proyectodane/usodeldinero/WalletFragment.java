package proyectodane.usodeldinero;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;


public class WalletFragment extends Fragment implements OnClickListener {

    /**
     * Importe total a de la billetera, con la suma de la carga
     */
    private String st_total;

    /**
     * Importe a cargar
     */
    private String st_subtotal;

    /**
     * ArrayList con todos los valores de billetes/monedas a cargar en la billetera
     */
    private ArrayList<String> newLoadMoneyValueNames;

    /**
     * Clase que se encarga de manejar lo referido al slide de imágenes y puntos
     */
    private ImageSlideManager imageSlideManager;

    /**
     * Clase que se encarga de manejar los mensajes emergentes
     */
    private SnackBarManager sb;

    /**
     * Vista instanciada
     */
    View rootView;


    // Todo: Ver si sirve este ejemplo par paso de parámetros. Si no se usa, borrarlo
    //        public WalletFragment() { }
    //
    //        /**
    //         * Devuelve una nueva instancia del Fragment, dado un número de sección
    //         */
    //        public static WalletFragment newInstance(int sectionNumber) {
    //            WalletFragment fragment = new WalletFragment();
    //            Bundle args = new Bundle();
    //            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
    //            fragment.setArguments(args);
    //            return fragment;
    //        }

    // Llamado en la parte inicial de la creación del Fragment.
    // Se inicializan objetos no gráficos.
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    // Llamado para usar el inflate con el layout del Fragment.
    // Se inicializa objetos gráficos
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        // Asigno los botones a escuchar
        Button addButton = (Button) rootView.findViewById(R.id.button14);
        addButton.setOnClickListener(this);
        Button saveButton = (Button) rootView.findViewById(R.id.button13);
        saveButton.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Inicio la lista de valores a cargar en la billetera
        newLoadMoneyValueNames = new ArrayList<String>();

        // Actualizo el valor del total, inicio el subtotal en cero y muestro en pantalla
        st_subtotal = getString(R.string.value_0);
        st_total = WalletManager.getInstance().obtainTotalCreditInWallet(getActivity());
        refreshSubtotalAndTotal();

        // Inicio el SnackBarManager para luego crear mensajes emergentes
        sb = new SnackBarManager();

        // Obtengo todos los valores a mostrar para la carga de la billetera
        ArrayList<String> moneyValueNames = WalletManager.getInstance().obtainMoneyValueNamesOfValidCurrency(getActivity());

        // Cargo el slide de imágenes y puntos indicadores
        // Parámetros:  + (1)Contexto
        //              + (2)ViewPager con su (3)FragmentManager y sus (4)moneyValueNames (nombres de las imágenes)
        //              + (5)LinearLayout y sus (6)(7)imágenes representando al punto
        imageSlideManager = new ImageSlideManager(getActivity(),
                (ViewPager) rootView.findViewById(R.id.pager_wallet),
                getActivity().getSupportFragmentManager(),
                moneyValueNames,
                (LinearLayout) rootView.findViewById(R.id.SliderDots_wallet),
                ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot),
                ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.nonactive_dot));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button14:
                sb.showTextShortOnClickActionDisabled(rootView.findViewById(R.id.coordinatorLayout_Wallet),getString(R.string.add_button_pressed),5);
                break;
            case R.id.button13:
                sb.showTextShortOnClickActionDisabled(rootView.findViewById(R.id.coordinatorLayout_Wallet),getString(R.string.save_button_pressed),5);
                break;
        }
    }


// TODO: Verificar si quedan o se sacan las funciones...
// TODO: y en caso de usarse deben pasarse dentro del onClick()

    /**
     * Cancela la carga en la billetera
     * */
    public void cancelLoad(View view) {
        sendToMain(view);
    }


    /**
     * Envía a la pantalla principal
     * */
    public void sendToMain(View view) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }


    /**
     * Muestra el texto de ayuda para este activity
     **/
    public void showHelp(View view) {
        sb.showTextIndefiniteOnClickActionDisabled(rootView.findViewById(R.id.coordinatorLayout_Wallet),getString(R.string.help_text_wallet),10);
    }


    /**
     * Actualiza el valor de la carga y del total
     **/
    public void refreshSubtotalAndTotal() {
        TextView textView = rootView.findViewById(R.id.textView6);
        textView.setText(getString(R.string.load_cash_sign) + st_subtotal + " - " + getString(R.string.total_cash_sign) + st_total);
    }


    /**
     * Sumo el valor seleccionado al subtotal de carga en billetera
     **/
    public void addValueToSubtotalTAB (View view) {

        // Obtengo el ID del valor elegido
        String st_valueID = imageSlideManager.getActualValueID();

        // Obtengo el valor monetario a partir del ID
        String st_value = WalletManager.getInstance().obtainValueFormID(getActivity(),st_valueID);

        // Agrego el ID a la lista para la futura carga, sumo el valor al subtotal y total para luego mostrarlo
        newLoadMoneyValueNames.add(st_valueID);
        st_subtotal = WalletManager.getInstance().addValues(st_value,st_subtotal);
        st_total = WalletManager.getInstance().addValues(st_value,st_total);
        refreshSubtotalAndTotal();

        // Creo el mensaje para notificar el valor seleccionado a sumar a la billetera y lo muestro
        String st_snackBarText = getString(R.string.value_selected_for_load) + st_value;
        sb.showTextShortOnClickActionDisabled(rootView.findViewById(R.id.coordinatorLayout_Wallet),st_snackBarText,2);
    }


    /**
     * Agrega la carga de dinero seleccionada en la billetera y luego envía a la pantalla principal
     * */
    public void addValuesToWallet (View view){

        // Guardo todos los valores seleccionados
        for(String currentNewLoadMoneyValueName : newLoadMoneyValueNames) {
            WalletManager.getInstance().addCurrencyInWallet(getActivity(),currentNewLoadMoneyValueName);
        }

        sendToMain(view);
    }


    /**
     * Envía a la pantalla de configuración
     * */
    public void sendToConfiguration(View view) {
        Intent intent = new Intent(getActivity(), ConfigurationActivity.class);
        startActivity(intent);
    }

}
