package br.edu.ifsp.scl.ads.pdm.contatos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import br.edu.ifsp.scl.ads.pdm.contatos.databinding.ActivityContatosBinding;
import br.edu.ifsp.scl.ads.pdm.contatos.databinding.ActivityContatosRvBinding;
import br.edu.ifsp.scl.ads.pdm.contatos.model.Contato;

import static android.provider.CalendarContract.CalendarCache.URI;

public class ContatosActivity extends AppCompatActivity implements OnContatoClickListener {
    //private ActivityContatosBinding activityContatosBinding;
    private ActivityContatosRvBinding activityContatosRvBinding;
    private ArrayList<Contato> contatosList;
    //private ContatosAdapter contatosAdapter;
    private ContatosRvAdapter contatosRvAdapter;
    private LinearLayoutManager contatosLinearLayoutManager;
    private final int NOVO_CONTATO_REQUEST_CODE = 0;
    private final int EDITAR_CONTATO_REQUEST_CODE = 1;

    private Contato contato;
    private final int CALL_PHONE_PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activityContatosBinding = ActivityContatosBinding.inflate(getLayoutInflater());
        //setContentView(activityContatosBinding.getRoot());
        activityContatosRvBinding = activityContatosRvBinding.inflate(getLayoutInflater());
        setContentView(activityContatosRvBinding.getRoot());

        //Instanciar o data sourece
        contatosList = new ArrayList<>();
        popularContatosList();

        // Instanciar o adpater
        /*contatosAdapter = new ContatosAdapter(
                this,
                R.layout.view_contato,
                contatosList
        );*/
        contatosRvAdapter = new ContatosRvAdapter(contatosList, this, getMenuInflater());

        // Associando o adpter com o listView
        //activityContatosBinding.contatosLv.setAdapter(contatosAdapter);

        // Instanciando o adpter com a listView
        contatosLinearLayoutManager = new LinearLayoutManager(this);

        // Associando o RV com adpter o layoutManager
        activityContatosRvBinding.contatosRv.setAdapter(contatosRvAdapter);
        activityContatosRvBinding.contatosRv.setLayoutManager(contatosLinearLayoutManager);

        // Registrando listView para menu de contexto
        //registerForContextMenu(activityContatosBinding.contatosLv);

        // Associar um lsiter de clique para o listview
        /*activityContatosBinding.contatosLv.setOnItemClickListener((parent, view, position, id) -> {
            // Abrindo tela para detalhes
           contato = contatosList.get(position);
           Intent detalhesIntent = new Intent(this, ContatoActivity.class);
           detalhesIntent.putExtra(Intent.EXTRA_USER, contato);
           startActivity(detalhesIntent);
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrando listView para menu de contexto
        //unregisterForContextMenu(activityContatosBinding.contatosRv);
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
                //contatosAdapter.notifyDataSetChanged();
                contatosRvAdapter.notifyDataSetChanged();
            }
        }else{
            if( requestCode == EDITAR_CONTATO_REQUEST_CODE && resultCode == RESULT_OK) {
                // Atualzia contato na lista
                Contato contato = (Contato) data.getSerializableExtra(Intent.EXTRA_USER);
                int posicao = data.getIntExtra(Intent.EXTRA_INDEX, -1);
                if (contato != null && posicao != -1) {
                    contatosList.remove(posicao);
                    contatosList.add(posicao, contato);
                    //contatosAdapter.notifyDataSetChanged();
                    contatosRvAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu_contato, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        //AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Pegando contato a partir da possição clicaca
        //contato = contatosAdapter.getItem(menuInfo.position);
        contato = contatosList.get(contatosRvAdapter.getPosicao());
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
            case R.id.editarContatoMi:
                Intent editarContatoIntent = new Intent(this, ContatoActivity.class);
                editarContatoIntent.putExtra(Intent.EXTRA_USER, contato);
                editarContatoIntent.putExtra(Intent.EXTRA_INDEX, contatosRvAdapter.getPosicao());
                startActivityForResult(editarContatoIntent, EDITAR_CONTATO_REQUEST_CODE);
                return true;
            case R.id.removerMi:
                contatosList.remove(contatosRvAdapter.getPosicao());
                contatosRvAdapter.notifyDataSetChanged();
                return true;
            default:
                return false;

        }
    }

    private void popularContatosList(){
        for (int i = 0; i < 5; i++){
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

    @Override
    public void onContatoClick(int posicao) {
        // Abrindo tela para detalhes
        contato = contatosList.get(posicao);
        Intent detalhesIntent = new Intent(this, ContatoActivity.class);
        detalhesIntent.putExtra(Intent.EXTRA_USER, contato);
        startActivity(detalhesIntent);
    }
}
