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

    // Request code para permissao de callphone
    private final int CALL_PHONE_PERMISSION_REQUEST_CODE = 1;

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
        }else if(view.getId() == activityContatoBinding.enviarEmailBt.getId()){
            //Eviando email
            Intent enviarEmailIntent = new Intent(Intent.ACTION_SEND);
            enviarEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { activityContatoBinding.emailEt.getText().toString()});
            enviarEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Título do email");
            enviarEmailIntent.putExtra(Intent.EXTRA_TEXT, "Corpo do email");
            enviarEmailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(enviarEmailIntent, "Enviar email"));
        }else if(view.getId() == activityContatoBinding.ligarTelefoneBt.getId()){
            // Fazendo uma ligação
            verifyCallPhonePermission();
        }else if(view.getId() == activityContatoBinding.acessarsiteBt.getId()){
            // Abrindo navegador
            Intent abrirNavegadorIntent = new Intent(Intent.ACTION_VIEW);
            abrirNavegadorIntent.setData(Uri.parse(activityContatoBinding.siteEt.getText().toString()));
            startActivity(abrirNavegadorIntent);
        }else if(view.getId() == activityContatoBinding.exportarpdfBt.getId()){
            //Implementar em aula
            Toast.makeText(this, "PDF exportado", Toast.LENGTH_LONG).show();
        }
    }

    private void verifyCallPhonePermission() {
        Intent ligarIntent = new Intent(Intent.ACTION_CALL);
        ligarIntent.setData(Uri.parse("tel:" + activityContatoBinding.telefoneEt.getText().toString()));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                // Usuário ja concedeu a permissão
                startActivity(ligarIntent);
            }
            else {
                // Solicitar permissão para o usuário em tempo de execução
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION_REQUEST_CODE);

            }
        }else{
            // A permissão foi solicitada no Manifest (Android < M)
            startActivity(ligarIntent);
        }
    }
}