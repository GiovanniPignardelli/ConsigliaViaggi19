package it.gpgames.consigliaviaggi19.network;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**La classe si occupa di verificare lo stato della connessione. Si tratta di una classe singleton.
 * Estende la classe BroadcastReceiver, quindi implementa il metodo onReceive().
 * Tutte le activity che intendono essere aggiornate sullo stato della conessione, devono registrarsi al receiver.
 * Un'activity che intende non essere più aggiornata sullo stato della connessione, deve efferruare l'unregister.*/
public class NetworkChangeReceiver extends BroadcastReceiver {

    /**Tutte le activity che si registrano, ottengono la medesima istanza di NetWorkChangeReceiver*/
    private static NetworkChangeReceiver networkChangeReceiverInstance;

    /**Costruttore privato per impedire che la classe venga istanziata dall'esterno.*/
    private NetworkChangeReceiver()
    {
        //private constructor;
    }

    public static NetworkChangeReceiver getNetworkChangeReceiverInstance()
    {
        if(networkChangeReceiverInstance==null)
        {
            networkChangeReceiverInstance=new NetworkChangeReceiver();
        }
        return networkChangeReceiverInstance;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("Network", "Testing internet connection");
        int status = NetworkUtil.getConnectivityStatus(context.getApplicationContext());
        Log.d("Network", NetworkUtil.getConnectivityStatusString(status));
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status==NetworkUtil.TYPE_NOT_CONNECTED) {
                Log.d("Network","There is not connection");
                Intent toNoConnectionActivity=new Intent(context.getApplicationContext(),NoConnectionActivity.class);
                toNoConnectionActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(toNoConnectionActivity);
            }
        }
    }

    public static boolean isConnected(Context context)
    {
        int conStatus=NetworkUtil.getConnectivityStatus(context);
        if(conStatus==NetworkUtil.TYPE_MOBILE || conStatus==NetworkUtil.TYPE_WIFI)
            return true;
        else
            return false;
    }

    /**Inner class che contiene metodi e attributi utili al controllo della connessione*/
    public static class NetworkUtil {

        private static final int TYPE_WIFI = 1;
        private static final int TYPE_MOBILE = 2;
        private static final int TYPE_NOT_CONNECTED = 0;

        /** Restituisce un intero a seconda della connessione rilevata
         * @param context Context del quale si testa la connessione*/
        public static int getConnectivityStatus(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI;

                if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE;
            }
            return TYPE_NOT_CONNECTED;
        }

        /**Restituisce una stringa che descrive lo stato della connessione
         * @param status intero che rappresenta lo stato della connessione (membro static final della classe)*/
        private static String getConnectivityStatusString(int status) {
            String statusString = null;
            if (status == NetworkUtil.TYPE_WIFI) {
                statusString = "Wifi enabled";
            } else if (status == NetworkUtil.TYPE_MOBILE) {
                statusString = "Mobile data enabled";
            } else if (status == NetworkUtil.TYPE_NOT_CONNECTED) {
                statusString = "Not connected to Internet";
            }
            return statusString;
        }
    }
}
