package mx.edu.uteq.dapps.proyectofinaldappszavaleta.ui.cuenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import mx.edu.uteq.dapps.proyectofinaldappszavaleta.R;

public class CambiaPassModal extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.cambia_pass_modal,
                container, false
        );

        return rootView;
    }
}
