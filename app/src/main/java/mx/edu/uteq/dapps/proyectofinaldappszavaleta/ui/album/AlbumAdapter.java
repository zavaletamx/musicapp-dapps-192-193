package mx.edu.uteq.dapps.proyectofinaldappszavaleta.ui.album;

import android.content.Context;
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

public class AlbumAdapter extends BaseAdapter {

    private Context contexto;
    private JSONArray datos;
    private LayoutInflater inflater;

    private ImageView ivAlbum;
    private TextView tvAlbum;
    private TextView tvInter;
    private TextView tvFecha;
    private Switch swLista;
    private ImageButton btnInfo;

    private SharedPreferences prefs;
    private String usuarioIdSesion;

    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    public AlbumAdapter(Context contexto, JSONArray datos) {
        this.contexto = contexto;
        this.datos = datos;
        inflater = LayoutInflater.from(contexto);
        prefs = contexto.getSharedPreferences("musicapp", Context.MODE_PRIVATE);
        usuarioIdSesion = prefs.getString("usuario_id", null);
        conexionServ = Volley.newRequestQueue(contexto);
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
                    R.layout.album_item,
                    null
            );

            ivAlbum = convertView.findViewById(R.id.iv_album);
            tvAlbum = convertView.findViewById(R.id.tv_album);
            tvInter = convertView.findViewById(R.id.tv_inter);
            tvFecha = convertView.findViewById(R.id.tv_fecha);
            swLista = convertView.findViewById(R.id.sw_lista);
            btnInfo = convertView.findViewById(R.id.btn_info);

            try {

                final String urlImagen = datos.getJSONObject(position).getString("img_album");
                final String albumId = datos.getJSONObject(position).getString("album_id");
                final String nombreAlbum = datos.getJSONObject(position).getString("nombre_album");
                final String nombreInter = datos.getJSONObject(position).getString("nombre_inter");
                final String fecha = datos.getJSONObject(position).getString("fecha_lanzamiento");
                final String usuarioId = datos.getJSONObject(position).getString("lista_deseo");

                Picasso.with(contexto).load(urlImagen).placeholder(R.drawable.placeholder).into(ivAlbum);
                tvAlbum.setText(nombreAlbum);
                tvInter.setText(nombreInter);
                tvFecha.setText(fecha);

                if (!usuarioId.equals("null")) {
                    swLista.setChecked(true);
                }
                
                btnInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contexto.startActivity(
                                new Intent(
                                        contexto,
                                        AlbumDetalleActivity.class
                                )
                                .putExtra("album_id", albumId)
                                .putExtra("img_album", urlImagen)
                                .putExtra("nombre_album", nombreAlbum)
                                .putExtra("nombre_inter", nombreInter)
                                .putExtra("fecha", fecha)
                        );
                    }
                });

                final View finalConvertView = convertView;
                swLista.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        final String accion = isChecked ? "agregar" : "eliminar";
                        final String accionTexto = isChecked ? "agregado" : "eliminado";
                        peticionServ = new StringRequest(
                                Request.Method.POST,
                                "https://wikiclod.mx/dapps/api-192/listadeseos/actualizar",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject objRespuesta = new JSONObject(response);

                                            if (objRespuesta.getInt("response_code") == 200) {
                                                Snackbar.make(
                                                        finalConvertView,
                                                        nombreAlbum + " " + accionTexto + " a la lista de deseos",
                                                        Snackbar.LENGTH_SHORT
                                                ).show();
                                            }
                                        }

                                        catch(Exception e) {
                                            Toast.makeText(contexto, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(contexto, error.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                        ){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("usuario_id", usuarioIdSesion);
                                params.put("album_id", albumId);
                                params.put("accion", accion);

                                return params;
                            }
                        };
                        conexionServ.add(peticionServ);
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
