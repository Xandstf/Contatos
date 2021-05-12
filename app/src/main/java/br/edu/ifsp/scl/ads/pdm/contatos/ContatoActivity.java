package br.edu.ifsp.scl.ads.pdm.contatos;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.edu.ifsp.scl.ads.pdm.contatos.databinding.ActivityContatoBinding;
import br.edu.ifsp.scl.ads.pdm.contatos.model.Contato;

public class ContatoActivity extends AppCompatActivity {
    // Instância da classe de View Binding
    private ActivityContatoBinding activityContatoBinding;

    // Cria variavel de novo contato
    Contato novoContato = new Contato();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContatoBinding = activityContatoBinding.inflate(getLayoutInflater());
        setContentView(activityContatoBinding.getRoot());
    }

    public void onClickButton(View view){
        novoContato = new Contato(
                activityContatoBinding.nomeEt.getText().toString(),
                activityContatoBinding.emailEt.getText().toString(),
                activityContatoBinding.telefoneEt.getText().toString(),
                activityContatoBinding.telefoneCelularEt.getText().toString(),
                false,
                activityContatoBinding.siteEt.getText().toString()
        );
        if(activityContatoBinding.tipoTelefoneSp.getSelectedItem().toString().equals("Residencial")){
            novoContato.setComercial(false);
        }else{
            novoContato.setComercial(true);
        }

        if(view.getId() == activityContatoBinding.salvarBt.getId()){
            Intent retornoIntent = new Intent();
            retornoIntent.putExtra(Intent.EXTRA_USER, novoContato);
            setResult(RESULT_OK, retornoIntent);
            finish();
        }else if(view.getId() == activityContatoBinding.exportarpdfBt.getId()){
            //Implementar em aula
            Toast.makeText(this, "PDF exportado", Toast.LENGTH_LONG).show();
        }
    }

}