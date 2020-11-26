package mx.edu.uteq.dapps.proyectofinaldappszavaleta.ui.cuenta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mx.edu.uteq.dapps.proyectofinaldappszavaleta.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CuentaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CuentaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputEditText tietTel;
    private ImageView ivUserpic;
    private Button btnCambiaImagen;
    private Button btnCambiaPass;

    /*Tomamos la info guardada en Sharedpreferences
    para cambiar la info del usuario*/
    private SharedPreferences prefs;

    private RequestQueue conexionServ;
    private StringRequest peticionServ;

    private ProgressDialog progress;

    public CuentaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MicuentaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CuentaFragment newInstance(String param1, String param2) {
        CuentaFragment fragment = new CuentaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_cuenta, container, false);

        tietTel = rootView.findViewById(R.id.tiet_tel);
        ivUserpic = rootView.findViewById(R.id.iv_userpic);
        btnCambiaImagen = rootView.findViewById(R.id.btn_cambia_imagen);
        btnCambiaPass = rootView.findViewById(R.id.btn_cambia_pass);

        progress = new ProgressDialog(getActivity());
        conexionServ = Volley.newRequestQueue(getActivity());

        // Inflate the layout for this fragment
        progress.setTitle("Cargando");
        progress.setMessage("Por favor espera...");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        prefs = getActivity().getSharedPreferences("musicapp", Context.MODE_PRIVATE);

        String usuarioId = prefs.getString("usuario_id", null);

        peticionServ = new StringRequest(
                Request.Method.POST,
                "https://wikiclod.mx/dapps/api-192/cuenta/datos/" + usuarioId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject objResp = new JSONObject(response);
                            if (objResp.getInt("response_code") == 200) {
                                JSONObject datosUsuario = objResp.getJSONObject("datos_usuario");
                                tietTel.setText(datosUsuario.getString("tel"));

                                if (datosUsuario.getString("userpic") != null) {
                                    Picasso
                                            .with(getActivity())
                                            .load(
                                                    datosUsuario.getString("userpic")
                                            ).into(ivUserpic);
                                }
                            }
                        } catch(Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        progress.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hide();
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

        btnCambiaImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CambiaImagenModal cim = new CambiaImagenModal();
                cim.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");

            }
        });

        btnCambiaPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CambiaPassModal cpm = new CambiaPassModal();
                cpm.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
            }
        });

        return rootView;
    }
}