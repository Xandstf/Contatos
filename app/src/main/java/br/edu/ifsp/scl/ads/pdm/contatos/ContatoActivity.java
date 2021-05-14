package br.edu.ifsp.scl.ads.pdm.contatos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.edu.ifsp.scl.ads.pdm.contatos.databinding.ActivityContatoBinding;
import br.edu.ifsp.scl.ads.pdm.contatos.model.Contato;

public class ContatoActivity extends AppCompatActivity {
    // Instância da classe de View Binding
    private ActivityContatoBinding activityContatoBinding;

    // Cria variavel de novo contato
    Contato novoContato = new Contato();
    private int posicao = -1;

    private final int PERMISSAO_ESCRITA_ARMAZENAMENTO_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContatoBinding = activityContatoBinding.inflate(getLayoutInflater());
        setContentView(activityContatoBinding.getRoot());

        // Esconde botões para ativar somente os necessarios
        activityContatoBinding.salvarBt.setVisibility(View.GONE);
        activityContatoBinding.exportarpdfBt.setVisibility(View.GONE);

        // Verifica se algum contato foi recebido
        novoContato = (Contato) getIntent().getSerializableExtra(Intent.EXTRA_USER);
        if(novoContato != null){
            // Identifica qual posicao foi clicada
            posicao = getIntent().getIntExtra(Intent.EXTRA_INDEX, -1);

            // Alterando a ativacao das views
            boolean ativo = (posicao != -1)? true : false;
            alterarAtivacaoViews(ativo);
            if (ativo) {
                getSupportActionBar().setSubtitle("Edição de contato");
                activityContatoBinding.salvarBt.setVisibility(View.VISIBLE);
                activityContatoBinding.salvarBt.setText("Atualizar");
            }else{
                getSupportActionBar().setSubtitle("Detalhes do contato");
                activityContatoBinding.exportarpdfBt.setVisibility(View.VISIBLE);
                activityContatoBinding.fecharBt.setVisibility(View.VISIBLE);
            }

            // Usando dados do contato d=para preencher os valores da minha view
            activityContatoBinding.nomeEt.setText(novoContato.getNomeCompleto());
            activityContatoBinding.emailEt.setText(novoContato.getEmail());
            activityContatoBinding.telefoneEt.setText(novoContato.getTelefone());
            activityContatoBinding.telefoneCelularEt.setText(novoContato.getCelular());
            activityContatoBinding.siteEt.setText(novoContato.getSite());
            if(novoContato.isComercial()){
                activityContatoBinding.tipoTelefoneSp.setSelection(1);
            }

        }else{
            activityContatoBinding.salvarBt.setVisibility(View.VISIBLE);
            getSupportActionBar().setSubtitle("Novo contato");
        }
    }

    private void alterarAtivacaoViews(boolean ativo){
        activityContatoBinding.nomeEt.setEnabled(ativo);
        activityContatoBinding.emailEt.setEnabled(ativo);
        activityContatoBinding.telefoneEt.setEnabled(ativo);
        activityContatoBinding.telefoneCelularEt.setEnabled(ativo);
        activityContatoBinding.siteEt.setEnabled(ativo);
        activityContatoBinding.tipoTelefoneSp.setEnabled(ativo);
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
            retornoIntent.putExtra(Intent.EXTRA_INDEX, posicao);
            setResult(RESULT_OK, retornoIntent);
            finish();
        }else if(view.getId() == activityContatoBinding.exportarpdfBt.getId()){
            verifyWriteAndSavePermission();
            Toast.makeText(this, "PDF salvo!", Toast.LENGTH_SHORT).show();
            finish();
        }else if(view.getId() == activityContatoBinding.fecharBt.getId()){
            finish();
        }
    }

    private void verifyWriteAndSavePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSAO_ESCRITA_ARMAZENAMENTO_REQUEST_CODE);
            }
            else {
                gerarDocumentoPdf();

            }
        }else{
            gerarDocumentoPdf();
        }
    }

    private void gerarDocumentoPdf(){
        //Escondendo botões
        activityContatoBinding.exportarpdfBt.setVisibility(View.GONE);
        activityContatoBinding.fecharBt.setVisibility(View.GONE);

        // Pegando a altura e a largura da View raiz
        View conteudo = activityContatoBinding.getRoot();
        int largura = conteudo.getWidth();
        int altura = conteudo.getHeight();

        // Criando o PDF
        PdfDocument documentoPdf = new PdfDocument();
        //Configurando a página/iniciando
        PdfDocument.PageInfo configuracaoPagina = new PdfDocument.PageInfo.Builder(largura, altura, 1).create();
        PdfDocument.Page pagina = documentoPdf.startPage(configuracaoPagina);

        // Criando um screenshot da view na página PDF de fato
        conteudo.draw(pagina.getCanvas());

        documentoPdf.finishPage(pagina);

        //Salvando arquivo PDF
        File diretorioDocumentos = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        try {
            File documento = new File(diretorioDocumentos, novoContato.getNomeCompleto().replace(" ", "_") + ".pdf");
            documento.createNewFile();
            documentoPdf.writeTo(new FileOutputStream(documento));
            documentoPdf.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSAO_ESCRITA_ARMAZENAMENTO_REQUEST_CODE){
            verifyWriteAndSavePermission();
        }
    }
}