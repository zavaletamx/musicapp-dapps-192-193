package mx.edu.uteq.dapps.proyectofinaldappszavaleta.ui.listadeseos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mx.edu.uteq.dapps.proyectofinaldappszavaleta.R;
import mx.edu.uteq.dapps.proyectofinaldappszavaleta.ui.album.AlbumDetalleActivity;

public class ListaDeseosAdapter extends BaseAdapter {

    private Context contexto;
    private JSONArray datos;
    private LayoutInflater inflater;

    private ImageView ivAlbum;
    private TextView tvAlbum;
    private TextView tvInter;
    private TextView tvFecha;
    private ImageButton btnEliminar;

    private SharedPreferences prefs;
    private String usuarioIdSesion;
    private AlertDialog.Builder alerta;

    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    public ListaDeseosAdapter(Context contexto, JSONArray datos) {
        this.contexto = contexto;
        this.datos = datos;
        inflater = LayoutInflater.from(contexto);
        prefs = contexto.getSharedPreferences("musicapp", Context.MODE_PRIVATE);
        usuarioIdSesion = prefs.getString("usuario_id", null);
        conexionServ = Volley.newRequestQueue(contexto);
        alerta = new AlertDialog.Builder(contexto);
    }

    @Override
    public int getCount() {
        return datos.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.listadeseos_item,
                    null
            );

            ivAlbum = convertView.findViewById(R.id.iv_album);
            tvAlbum = convertView.findViewById(R.id.tv_album);
            tvInter = convertView.findViewById(R.id.tv_inter);
            tvFecha = convertView.findViewById(R.id.tv_fecha);
            btnEliminar = convertView.findViewById(R.id.btn_eliminar);

            try {

                final String urlImagen = datos.getJSONObject(position).getString("img_album");
                final String albumId = datos.getJSONObject(position).getString("album_id");
                final String nombreAlbum = datos.getJSONObject(position).getString("nombre_album");
                final String nombreInter = datos.getJSONObject(position).getString("nombre_inter");
                final String fecha = datos.getJSONObject(position).getString("fecha_lanzamiento");

                Picasso.with(contexto).load(urlImagen).placeholder(R.drawable.placeholder).into(ivAlbum);
                tvAlbum.setText(nombreAlbum);
                tvInter.setText(nombreInter);
                tvFecha.setText(fecha);

                btnEliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alerta.setTitle("Eliminar --" + nombreAlbum+"--")
                                .setMessage("¿Realmente deseas eliminar este álbum de tu lista de deseos?\nEsta acción no puede deshacerse")
                                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .setCancelable(false)
                                .setIcon(R.drawable.ic_delete_forever_red)
                                .show();
                    }
                });
            }

            catch(Exception e) {
                Toast.makeText(contexto, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
        return convertView;
    }
}
