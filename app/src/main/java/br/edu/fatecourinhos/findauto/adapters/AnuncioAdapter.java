package br.edu.fatecourinhos.findauto.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.model.Anuncio;
import br.edu.fatecourinhos.findauto.model.Cidade;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 *
 * @author Guilherme Montanher
 * @since 11/07/16
 */
public class AnuncioAdapter extends BaseAdapter {

    private ArrayList<Anuncio> anuncios;
    private Context context;

    public AnuncioAdapter(ArrayList<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @Override
    public int getCount() {
        return anuncios.size();
    }

    @Override
    public Object getItem(int position) {
        return anuncios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return anuncios.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Anuncio anuncio = anuncios.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_anuncio, null);

        ImageView imageAnuncio = (ImageView) view.findViewById(R.id.adapterAnuncioImage);
        ImageView imageTipo = (ImageView) view.findViewById(R.id.adapterAnuncioImageTipo);
        TextView textViewTitulo = (TextView) view.findViewById(R.id.adapterAnuncioTextViewTitulo);
        TextView textViewPreco = (TextView) view.findViewById(R.id.adapterAnuncioTextViewPreco);
        TextView textViewData = (TextView) view.findViewById(R.id.adapterAnuncioTextViewData);

        if (!anuncio.getFotoPrincipal().equals("NULL")) {
            Glide.with(context)
                    .load(EndPoints.BASE_URL + "/" + anuncio.getFotoPrincipal())
                    //.error(R.drawable.ic_motorcycle_black_48dp)
                    //.bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                    //.skipMemoryCache(true)
                    .into(imageAnuncio);
        }

        switch (anuncio.getTipo()){
            case 1:
                imageTipo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_motorcycle_black_24dp, null));
                break;
            case 2:
                imageTipo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_car_black, null));
                break;
            case 3:
                imageTipo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_local_shipping_black_48dp, null));
                break;
        }

        textViewTitulo.setText(anuncio.getTitulo());
        textViewPreco.setText("R$ "+String.format("%.2f",anuncio.getPreco()));

        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTime(anuncio.getData());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        String monthStr = "";
        switch (month){
            case 1:
                monthStr = "Janeiro";
                break;
            case 2:
                monthStr = "Fevereiro";
                break;
            case 3:
                monthStr = "Março";
                break;
            case 4:
                monthStr = "Abril";
                break;
            case 5:
                monthStr = "Maio";
                break;
            case 6:
                monthStr = "Junho";
                break;
            case 7:
                monthStr = "Julho";
                break;
            case 8:
                monthStr = "Agosto";
                break;
            case 9:
                monthStr = "Setembro";
                break;
            case 10:
                monthStr = "Outubro";
                break;
            case 11:
                monthStr = "Novembro";
                break;
            case 12:
                monthStr = "Dezembro";
                break;
        }

        String strHour = String.valueOf(hour);
        String strMinute = String.valueOf(minute);

        while(strHour.length() < 2)
            strHour = "0"+strHour;

        while(strMinute.length() < 2)
            strMinute = "0"+strMinute;

        textViewData.setText(day+" "+monthStr+" "+strHour+":"+strMinute);

        return view;
    }

    @Override
    public boolean isEnabled (int position) {
        return true;
    }
}
