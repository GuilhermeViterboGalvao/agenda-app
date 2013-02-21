package br.com.agenda.util;

import java.util.ArrayList;
import java.util.List;
import br.com.agenda.AgendaApp;
import br.com.agenda.ListaDeContatosActivity;
import br.com.agenda.R;
import br.com.agenda.bean.Contato;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterListaDeContatos extends ArrayAdapter<Contato> {
	
	public AdapterListaDeContatos(Context context, int textViewResourceId, AgendaApp app){
		super(context, textViewResourceId);
		downloadTasks = new ArrayList<DownloadTask>();
		this.app = app;
	}
	
	public AdapterListaDeContatos(Context context, int textViewResourceId, List<Contato> list, AgendaApp app){
		super(context, textViewResourceId, list);
		downloadTasks = new ArrayList<DownloadTask>();
		this.app = app;
	}
	
	private List<DownloadTask> downloadTasks;
	
	private LayoutInflater inflater;
	
	private ImageView imvContato;
	
	private TextView txvNome;
	
	private Button btnEditar;

	private AgendaApp app;
	
	public void addAll(List<Contato> contatos){
		for (int i = 0; i < contatos.size(); i++) {
			add(contatos.get(i));
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_listadecontatos, parent, false);
		}
		
		Contato contato = getItem(position);
		
		txvNome = (TextView)convertView.findViewById(R.itemListaDeContatos.txvNome);
		txvNome.setText(contato.getNome());
		
		btnEditar = (Button)convertView.findViewById(R.itemListaDeContatos.btnEditar);
		btnEditar.setOnClickListener((ListaDeContatosActivity)getContext());
		btnEditar.setTag(contato);
		
		imvContato = (ImageView)convertView.findViewById(R.itemListaDeContatos.imvContato);
		imvContato.setTag(position);			

		if (contato.getCaminhoFoto() != null) {
			String url = GerenciadorDeDados.urlContatoFoto + "?idContato=" + contato.getCodigo();
			
			Bitmap bmp = app.getImagem(url); 
			
			if (bmp != null) {
				imvContato.setImageBitmap(bmp);
			} else {
				DownloadTask download = new DownloadTask(position, imvContato, R.drawable.unknown, app);
				download.execute(url);
				downloadTasks.add(download);
			}
		}
		
		return convertView;
	}
	
	public void stopDownloads(){
		if (downloadTasks != null) {			
			int size = downloadTasks.size();
			DownloadTask download = null;			
			if (size > 0) {				
				for (int i = 0; i < size; i++) {
					download = downloadTasks.get(i);
					if (!download.isCancelled()) {
						download.cancel(true);
					}
				}				
				downloadTasks.clear();
			}
		}
	}
}