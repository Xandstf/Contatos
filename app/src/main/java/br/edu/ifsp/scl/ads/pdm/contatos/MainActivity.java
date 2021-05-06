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

import br.edu.ifsp.scl.ads.pdm.contatos.databinding.ActivityMainBinding;
import br.edu.ifsp.scl.ads.pdm.contatos.model.Contato;

public class MainActivity extends AppCompatActivity {
    // Instância da classe de View Binding
    private ActivityMainBinding activityMainBinding;

    // Request code para permissao de callphone
    private final int CALL_PHONE_PERMISSION_REQUEST_CODE = 1;

    // Cria variavel de novo contato
    Contato novoContato = new Contato();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.telefoneCelularEt.setVisibility(View.GONE);
        activityMainBinding.toggleTelefoneBt.setText("Adicionar celular");
    }

    public void onClickButton(View view){
        if(view.getId() == activityMainBinding.salvarBt.getId()){
            //Implementar em aula
            novoContato.setNomeCompleto(activityMainBinding.nomeEt.getText().toString());
            novoContato.setEmail(activityMainBinding.emailEt.getText().toString());
            novoContato.setTelefone(activityMainBinding.telefoneEt.getText().toString());
            if(activityMainBinding.tipoTelefoneSp.getSelectedItem().toString().equals("Residencial")){
                novoContato.setComercial(false);
            }else{
                novoContato.setComercial(true);
            }
            if(activityMainBinding.toggleTelefoneBt.getText().equals("Remover")){
                novoContato.setCelular(activityMainBinding.telefoneCelularEt.getText().toString());
            }else{
                novoContato.setCelular("N/D");
            }
            novoContato.setSite(activityMainBinding.siteEt.getText().toString());

            Toast.makeText(this, "Contato salvo", Toast.LENGTH_LONG).show();
        }else if(view.getId() == activityMainBinding.enviarEmailBt.getId()){
            //Eviando email
            Intent enviarEmailIntent = new Intent(Intent.ACTION_SEND);
            enviarEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { activityMainBinding.emailEt.getText().toString()});
            enviarEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Título do email");
            enviarEmailIntent.putExtra(Intent.EXTRA_TEXT, "Corpo do email");
            enviarEmailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(enviarEmailIntent, "Enviar email"));
        }else if(view.getId() == activityMainBinding.ligarTelefoneBt.getId()){
            // Fazendo uma ligação
            verifyCallPhonePermission();
        }else if(view.getId() == activityMainBinding.acessarsiteBt.getId()){
            // Abrindo navegador
            Intent abrirNavegadorIntent = new Intent(Intent.ACTION_VIEW);
            abrirNavegadorIntent.setData(Uri.parse(activityMainBinding.siteEt.getText().toString()));
            startActivity(abrirNavegadorIntent);
        }else if(view.getId() == activityMainBinding.exportarpdfBt.getId()){
            //Implementar em aula
            Toast.makeText(this, "PDF exportado", Toast.LENGTH_LONG).show();
        }else if(view.getId() == activityMainBinding.toggleTelefoneBt.getId()){
            if(activityMainBinding.toggleTelefoneBt.getText().equals("Remover")){
                activityMainBinding.telefoneCelularEt.setVisibility(View.GONE);
                activityMainBinding.telefoneCelularEt.setText("");
                activityMainBinding.toggleTelefoneBt.setText("Adicionar celular");
            }else{
                activityMainBinding.telefoneCelularEt.setVisibility(View.VISIBLE);
                activityMainBinding.toggleTelefoneBt.setText("Remover");
            }
        }
    }

    private void verifyCallPhonePermission() {
        Intent ligarIntent = new Intent(Intent.ACTION_CALL);
        ligarIntent.setData(Uri.parse("tel:" + activityMainBinding.telefoneEt.getText().toString()));
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