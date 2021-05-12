package br.edu.ifsp.scl.ads.pdm.contatos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import br.edu.ifsp.scl.ads.pdm.contatos.databinding.ActivityContatoBinding;
import br.edu.ifsp.scl.ads.pdm.contatos.databinding.ActivityContatosBinding;
import br.edu.ifsp.scl.ads.pdm.contatos.model.Contato;

import static android.provider.CalendarContract.CalendarCache.URI;

public class ContatosActivity extends AppCompatActivity {
    private ActivityContatosBinding activityContatosBinding;
    private ArrayList<Contato> contatosList;
    private ContatosAdapter contatosAdapter;
    private final int NOVO_CONTATO_REQUEST_CODE = 0;

    private Contato contato;
    // Request code para permissao de callphone
    private final int CALL_PHONE_PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContatosBinding = ActivityContatosBinding.inflate(getLayoutInflater());
        setContentView(activityContatosBinding.getRoot());

        //Instanciar o data sourece
        contatosList = new ArrayList<>();
        //popularContatosList();

        // Instanciar o adpater
        contatosAdapter = new ContatosAdapter(
                this,
                R.layout.view_contato,
                contatosList
        );

        // Associando o adpter com o listView
        activityContatosBinding.contatosLv.setAdapter(contatosAdapter);

        // Registrando listView para menu de contexto
        registerForContextMenu(activityContatosBinding.contatosLv);
    }

    private void popularContatosList(){
        for (int i = 0; i < 20; i++){
            contatosList.add(
                    new Contato(
                            "Nome " + i,
                            "Email " + i,
                            "Telefone " + i,
                            "Celular " + i,
                            (i % 2 == 0) ? false : true,
                            "www.site" + i + ".com.br"
                    )
            );
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contatos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.novoContatoMi){
            Intent novoContatoIntent = new Intent(this, ContatoActivity.class);
            startActivityForResult(novoContatoIntent, NOVO_CONTATO_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == NOVO_CONTATO_REQUEST_CODE && resultCode == RESULT_OK){
            Contato contato = (Contato) data.getSerializableExtra(Intent.EXTRA_USER);
            if(contato != null){
                contatosList.add(contato);
                contatosAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu_contato, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Pegando contato a partir da possição clicaca
        contato = contatosAdapter.getItem(menuInfo.position);

        switch (item.getItemId()){
            case R.id.enviarEmailMi:
                Intent enviarEmailIntent = new Intent(Intent.ACTION_SENDTO, URI.parse("mailto: "));
                enviarEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String []{contato.getEmail()});
                enviarEmailIntent.putExtra(Intent.EXTRA_SUBJECT, contato.getNomeCompleto());
                enviarEmailIntent.putExtra(Intent.EXTRA_TEXT, contato.toString());
                startActivity(enviarEmailIntent);
                return true;
            case R.id.ligarMi:
                verifyCallPhonePermission();
                return true;
            case R.id.acessarSiteMi:
                Intent abrirNavegadorIntent = new Intent(Intent.ACTION_VIEW);
                abrirNavegadorIntent.setData(Uri.parse(contato.getSite()));
                startActivity(abrirNavegadorIntent);
                return true;
            case R.id.detalhesContatoMi:
                return true;
                //Junto em aula
            case R.id.editarContatoMi:
                return true;
                //Junto em aula
            case R.id.removerMi:
                return true;
                //Junto em aula
            default:
                return false;

        }
    }

    private void verifyCallPhonePermission() {
        Intent ligarIntent = new Intent(Intent.ACTION_CALL);
        ligarIntent.setData(Uri.parse("tel:" + contato.getTelefone()));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PHONE_PERMISSION_REQUEST_CODE){
            verifyCallPhonePermission();
        }
    }
}
