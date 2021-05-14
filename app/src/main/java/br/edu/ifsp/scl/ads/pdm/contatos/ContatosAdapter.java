package br.edu.ifsp.scl.ads.pdm.contatos;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import br.edu.ifsp.scl.ads.pdm.contatos.databinding.ViewContatoBinding;
import br.edu.ifsp.scl.ads.pdm.contatos.model.Contato;

public class ContatosAdapter extends ArrayAdapter<Contato> {
    public ContatosAdapter(Context contexto, int layout, ArrayList<Contato> contatosArrayList){
        super(contexto, layout, contatosArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewContatoBinding viewContatoBinding;
        ContatoViewHolder  contatoViewHolder;

        //Verificar se é necessario inflar (Criar nova celular)
        if(convertView == null) {
            //Inflei uma nova celula usando a classe de View Binding
            viewContatoBinding = ViewContatoBinding.inflate(LayoutInflater.from(getContext()));

            //Atribui a nova celula a view que sera devolvida preechida para o listView
            convertView = viewContatoBinding.getRoot();

            // Pega e guarda referencias para as views internas da celula usando um holder
            contatoViewHolder = new ContatoViewHolder();
            contatoViewHolder.nomeContatoTv = viewContatoBinding.nomeContatoTv;
            contatoViewHolder.emailContatoTv = viewContatoBinding.emailContatoTv;
            contatoViewHolder.telefoneContatoTv = viewContatoBinding.telefoneContatoTv;

            //Associa a view da celula ao Holder que referencia suas Views internas
            convertView.setTag(contatoViewHolder);

        }
        //Pega o holder associado a celula (nova ou reciclada)
        contatoViewHolder = (ContatoViewHolder) convertView.getTag();

        // Atualizar os valores dos TextViews
        Contato contato = getItem(position);
        contatoViewHolder.nomeContatoTv.setText(contato.getNomeCompleto());
        contatoViewHolder.emailContatoTv.setText(contato.getEmail());
        contatoViewHolder.telefoneContatoTv.setText(contato.getTelefone());

        return convertView;
    }

    private class ContatoViewHolder {
        public TextView nomeContatoTv;
        public TextView emailContatoTv;
        public TextView telefoneContatoTv;
    }
}
