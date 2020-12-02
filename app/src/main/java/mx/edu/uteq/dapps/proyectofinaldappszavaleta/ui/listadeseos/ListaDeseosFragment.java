package mx.edu.uteq.dapps.proyectofinaldappszavaleta.ui.listadeseos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mx.edu.uteq.dapps.proyectofinaldappszavaleta.R;
import mx.edu.uteq.dapps.proyectofinaldappszavaleta.ui.album.AlbumAdapter;

public class ListaDeseosFragment extends Fragment {

    private SharedPreferences prefs;
    private String usuarioId;

    private SwipeRefreshLayout srlAlbums;
    private ListView lvAlbums;

    private ListaDeseosAdapter adaptador;
    private JSONArray datos;

    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    public ListaDeseosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lista_deseos, container, false);
        prefs = getActivity().getSharedPreferences("musicapp", Context.MODE_PRIVATE);
        usuarioId = prefs.getString("usuario_id", null);

        srlAlbums = rootView.findViewById(R.id.srl_listadeos);
        lvAlbums = rootView.findViewById(R.id.lv_listadeseos);

        conexionServ = Volley.newRequestQueue(getActivity());

        datos = new JSONArray();

        adaptador = new ListaDeseosAdapter(getActivity(), datos);
        lvAlbums.setAdapter(adaptador);

        srlAlbums.post(new Runnable() {
            @Override
            public void run() {
                srlAlbums.setRefreshing(true);
                cargaListaDeseos();
            }
        });

        srlAlbums.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlAlbums.setRefreshing(true);
                cargaListaDeseos();
            }
        });
        return rootView;
    }

    private void cargaListaDeseos() {
        peticionServ = new StringRequest(
                Request.Method.POST,
                "https://wikiclod.mx/dapps/api-192/listadeseos/mostrar",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objRespuesta = new JSONObject(response);
                            if (objRespuesta.getInt("response_code") == 200) {

                                adaptador = new ListaDeseosAdapter(
                                    getActivity(),
                                    objRespuesta.getJSONArray("lista_deseos")
                                );
                                lvAlbums.setAdapter(adaptador);
                                /*Actualizar el adaptador del ListView*/
                                adaptador.notifyDataSetChanged();

                                if (objRespuesta.getJSONArray("lista_deseos").length() == 0) {
                                    Snackbar.make(
                                            getView(),
                                            "Tu lista de deseos está vacía",
                                            BaseTransientBottomBar.LENGTH_INDEFINITE
                                    ).show();
                                }
                            }
                        }

                        catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        srlAlbums.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        srlAlbums.setRefreshing(false);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuario_id", usuarioId);

                return params;
            }
        };
        conexionServ.add(peticionServ);
    }
}