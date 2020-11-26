package mx.edu.uteq.dapps.proyectofinaldappszavaleta.ui.cuenta;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mx.edu.uteq.dapps.proyectofinaldappszavaleta.R;

public class CambiaImagenModal extends BottomSheetDialogFragment {

    /*Tomamos la info guardada en Sharedpreferences
    para cambiar la info del usuario*/
    private SharedPreferences prefs;

    private TextInputEditText tietUripic;
    private ProgressBar pbCim;
    private LinearLayout llCargando;
    private LinearLayout llContenido;
    private Button btnGuardarUserpic;
    private RequestQueue conexionServ;
    private StringRequest peticionServ;
    private ImageView ivUserpic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        prefs = getActivity().getSharedPreferences("musicapp", Context.MODE_PRIVATE);

        String usuarioId = prefs.getString("usuario_id", null);

        View rootView = inflater.inflate(
                R.layout.cambia_imagen_modal,
                container, false
        );

        tietUripic = rootView.findViewById(R.id.tiet_uripic);
        pbCim = rootView.findViewById(R.id.pb_cim);
        llContenido = rootView.findViewById(R.id.ll_contenido);
        llCargando = rootView.findViewById(R.id.ll_cargando);
        btnGuardarUserpic = rootView.findViewById(R.id.btn_guardaruserpic);

        conexionServ = Volley.newRequestQueue(getActivity());

        btnGuardarUserpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llContenido.setVisibility(View.GONE);
                llCargando.setVisibility(View.VISIBLE);

                peticionServ = new StringRequest(
                        Request.Method.POST,
                        "https://wikiclod.mx/dapps/api-192/cuenta/editar/"+usuarioId,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject objRespuesta = new JSONObject(response);
                                    if (objRespuesta.getInt("response_code") == 200) {
                                        Toast.makeText(
                                                getActivity(),
                                                "Imagen actualizada",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                                        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

                                        ivUserpic = navigationView.getHeaderView(0).findViewById(R.id.image_view_avatar);

                                        Picasso
                                                .with(getActivity())
                                                .load(tietUripic.getText().toString())
                                                .into(ivUserpic);

                                        getActivity().finish();
                                        getActivity().overridePendingTransition(0, 0);
                                        getActivity().startActivity(getActivity().getIntent());
                                        getActivity().overridePendingTransition(0, 0);
                                    }

                                    else {
                                        Toast.makeText(
                                                getActivity(),
                                                response,
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                } catch(Exception e) {
                                    Toast.makeText(
                                            getActivity(),
                                            e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                                llCargando.setVisibility(View.GONE);
                                llContenido.setVisibility(View.VISIBLE);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(
                                        getActivity(),
                                        error.toString(),
                                        Toast.LENGTH_LONG
                                ).show();
                                llCargando.setVisibility(View.GONE);
                                llContenido.setVisibility(View.VISIBLE);
                            }
                        }
                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("usuario_id", usuarioId);
                        params.put("userpic", tietUripic.getText().toString());

                        return params;
                    }
                };

                conexionServ.add(peticionServ);
            }
        });



        return rootView;
    }
}
