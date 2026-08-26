package com.mds.aurad;

import android.app.Activity;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import java.util.*;

public class MdsView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final SharedPreferences prefs;
    private final Bitmap logo, intro1, intro2, page10Latin, page11Latin, page12Latin, page10Arab, page12Arab;
    private int page = 1;
    private boolean latin = false;
    private final Map<String,Integer> counts = new HashMap<>();
    private final int GOLD = Color.rgb(231, 174, 0);
    private final int BG = Color.rgb(5,5,5);

    public MdsView(Context c) {
        super(c);
        prefs = c.getSharedPreferences("mds_state", Context.MODE_PRIVATE);
        logo = load(c, R.drawable.logo_mds);
        intro1 = load(c, R.drawable.intro_1);
        intro2 = load(c, R.drawable.intro_2);
        page10Latin = load(c, R.drawable.page10_latin);
        page11Latin = load(c, R.drawable.page11_latin);
        page12Latin = load(c, R.drawable.page12_latin);
        page10Arab = load(c, R.drawable.page10_arab);
        page12Arab = load(c, R.drawable.page12_arab);
        String[] keys={"p4a","p4b","p5","p7","p8","p9"};
        for(String k:keys) counts.put(k,prefs.getInt(k,0));
        stroke.setStyle(Paint.Style.STROKE); stroke.setStrokeWidth(3); stroke.setColor(GOLD);
    }
    private Bitmap load(Context c,int id){return BitmapFactory.decodeResource(c.getResources(),id);}
    private float sx(){return getWidth()/941f;} private float sy(){return getHeight()/1672f;}

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        c.drawColor(BG);
        if(page==1){drawFull(c,intro1);return;}
        if(page==2){drawFull(c,intro2);return;}
        if(page==10){drawFull(c,latin?page10Latin:page10Arab);return;}
        if(page==11 && latin){drawFull(c,page11Latin);return;}
        if(page==12){drawFull(c,latin?page12Latin:page12Arab);return;}
        if(page==3){drawSelect(c);return;}
        if(page==11){drawTextPage(c, "التَوَسلُ", arab11(), true);return;}
        drawContent(c);
    }

    private void drawFull(Canvas c, Bitmap b){
        if(b==null)return;
        Rect src=new Rect(0,0,b.getWidth(),b.getHeight());
        RectF dst=new RectF(0,0,getWidth(),getHeight());
        c.drawBitmap(b,src,dst,p);
    }

    private void header(Canvas c,String title){
        float w=getWidth(), h=getHeight();
        c.drawRoundRect(new RectF(12,12,w-12,h-12),24,24,stroke);
        float size=Math.min(w*.22f,h*.16f);
        RectF lr=new RectF((w-size)/2,25,(w+size)/2,25+size);
        c.drawBitmap(logo,null,lr,p);
        p.setColor(GOLD);p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(w*.055f); c.drawText(title,w/2,25+size+52,p);
        p.setStrokeWidth(2);p.setColor(GOLD);c.drawLine(w*.12f,25+size+75,w*.88f,25+size+75,p);
    }

    private void drawSelect(Canvas c){
        header(c,"PILIH TEKS"); float w=getWidth(),h=getHeight();
        button(c,new RectF(w*.10f,h*.42f,w*.90f,h*.56f),"عَرَبِيّ   TEKS ARAB",true);
        button(c,new RectF(w*.10f,h*.60f,w*.90f,h*.74f),"Aa   TEKS LATIN",false);
    }

    private void drawContent(Canvas c){
        String title=""; String text=""; String key=null; int target=0; String second=null; String key2=null; int target2=0;
        if(page==4){ title=latin?"WIRID THORIQOH SYADZILIYYAH":"وِرْدُ الطَّرِيقَةِ الشَّاذِلِيَّةِ"; text=latin?"AUDZUBILLAHIMNASSYAITHONIRROJIIM (3X)":"(x٣)أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيم"; key="p4a"; target=3; second=latin?"BISMILLAAHIRROHMAANIRROHIIM (3X)":"(xبِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ (٣";key2="p4b";target2=3; }
        if(page==5){ title=""; text=latin?"WAMAA TUQODDIMUU LI ANFUSIKUM MIN KHOIRIN TAJIDUUHU 'INDALLOOHI HUWA KHOIRON WA A'ZHOMA AJRON WASTAGHFIRULLOOH. INNALLOOHA GHOFUURUR ROHIIM.":"وَمَا تُقَدِّمُوا لِأَنْفُسِكُمْ مِنْ خَيْرٍ تَجِدُوهُ عِنْدَ اللَّهِ هُوَ خَيْرًا وَأَعْظَمُ أَجْرًا وَاسْتَغْفِرُوا اللَّهَ إِنَّ اللَّهَ غَفُورٌ رَحِيمٌ."; second=latin?"ASTAGHFIRULLAAHAL 'AZHIIM (99X)":"(xأَسْتَغْفِرُ اللَّهَ الْعَظِيمَ (٩٩";key2="p5";target2=99; }
        if(page==6){ text=latin?"ASTAGHFIRULLAAHAL 'AZHIIM. ALLADZII LAA ILAAHA ILLAA HUWAL HAYYUL QOYYUUMU WA ATUUBU ILAIH":"أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ الَّذِي لَا إِلٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ"; second=latin?"INNALLAAHA WA MALAAIKATAHUU YUSHOLLUUNA 'ALAN NABIYYI .YAA AYYUHALLADZIINA AAMANUU SHOLLUU 'ALAIHI WA SALLIMUU TASLIIMAA.":"إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ يَا أَيُّهَا الَّذِينَ آمَنُوا صَلُّوا عَلَيْهِ وَسَلِّمُوا تَسْلِيمًا."; }
        if(page==7){ text=latin?"ALLAAHUMMA SHOLLI 'ALAA SAYYIDINAA MUHAMMADIN 'ABDIKA WA ROSUULIKAN NABIYYIL UMMIYYI WA 'ALAA AALIHI WA SHOHBIHI WA SALLIM (99X)":"اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ عَبْدِكَ وَرَسُولِكَ النَّبِيِّ الْأُمِّيِّ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلِّمْ (٩٩×)";key="p7";target=99;second=latin?"ALLAAHUMMA SHOLLI 'ALAA SAYYIDINAA MUHAMMADIN 'ABDIKA WA ROSUULIKAN NABIYYIL UMMIYYI WA 'ALAA AALIHI WA SHOHBIHI WA SALLIM TASLIIMAN BIQODRI 'AZHOMATI DZAATIKA FII KULLI WAQTIN WA HIIN.":"اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ عَبْدِكَ وَرَسُولِكَ النَّبِيِّ الْأُمِّيِّ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلِّمْ تَسْلِيمًا بِقَدْرِ عَظَمَةِ ذَاتِكَ فِي كُلِّ وَقْتٍ وَحِينٍ.";}
        if(page==8){title=latin?"FA'LAM ANNAHU":"فَاعْلَمْ أَنَّهُ";text=latin?"LAA ILAAHA ILLALLAAH (99X)":"(xلَا إِلٰهَ إِلَّا اللَّهُ (٩٩";key="p8";target=99;second=latin?"LAA ILAAHA ILLALLAAHU MUHAMMADUR ROSUULULLAAHI SHOLLALLAAHU 'ALAIHI WA 'ALAA AALIHI WA SHOHBIHI WA SALLAM.":"لَا إِلٰهَ إِلَّا اللَّهُ مُحَمَّدٌ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلَّمَ.";}
        if(page==9){ text=latin?"BISMILLAAHIRROHMAANIRROHIIM. QUL HUWALLOOHU AHAD. ALLOOHUSH SHOMAD. LAM YALID WA LAM YUULAD WA LAM YAKUN LAHUU KUFUWAN AHAD (3X)":"بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ.\nقُلْ هُوَ اللَّهُ أَحَدٌ.\nاللَّهُ الصَّمَدُ.\nلَمْ يَلِدْ وَلَمْ يُولَدْ وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ (٣×)";key="p9";target=3; }
        drawPage(c,title,text,key,target,second,key2,target2);
    }

    private void drawPage(Canvas c,String title,String first,String key,int target,String second,String key2,int target2){
        float w=getWidth(),h=getHeight(); header(c,title.isEmpty()?"":title); float top=h*.23f;
        if(!first.isEmpty()){
            top=drawParagraph(c,first,w*.08f,w*.92f,top,latin?40:46);
            if(key!=null){top=drawCounter(c,key,target,top+16);}
        }
        if(second!=null){
    top=drawParagraph(c,second,w*.08f,w*.92f,top+32,latin?38:44);
    if(key2!=null) top=drawCounter(c,key2,target2,top+20);
        }
        drawFooter(c);
    }

    private float drawParagraph(Canvas c,String s,float l,float r,float y,float size){
        p.setColor(Color.WHITE);p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD));p.setTextSize(size);
        if(!latin) p.setTypeface(Typeface.create("serif",Typeface.NORMAL));
        String[] lines=s.split("\\n");
        for(String raw:lines){
            List<String> out=wrap(raw,r-l,p);
            for(String q:out){c.drawText(q,(l+r)/2,y,p);y+=size*1.35f;}
        }
        return y;
    }
    private List<String> wrap(String s,float width,Paint paint){
        ArrayList<String> out=new ArrayList<>(); String[] words=s.trim().split(" ");String cur="";
        for(String z:words){String n=cur.isEmpty()?z:cur+" "+z;if(paint.measureText(n)>width && !cur.isEmpty()){out.add(cur);cur=z;}else cur=n;}if(!cur.isEmpty())out.add(cur);return out;
    }
    private float drawCounter(Canvas c, String key, int target, float y) {
    float w = getWidth();
    int n = counts.get(key);

    p.setTextAlign(Paint.Align.CENTER);
    p.setTypeface(Typeface.DEFAULT_BOLD);

    // Angka hitungan
    p.setColor(GOLD);
    p.setTextSize(w * .055f);
    c.drawText(n + " / " + target, w / 2, y, p);

    y += w * .025f;

    // Tombol HITUNG lebih besar
    RectF hit = new RectF(
        w * .22f,
        y,
        w * .78f,
        y + w * .19f
    );

    c.drawOval(hit, stroke);

    p.setColor(Color.WHITE);
    p.setTextSize(w * .055f);
    c.drawText(
        "HITUNG",
        w / 2,
        hit.centerY() + p.getTextSize() * .35f,
        p
    );

    y = hit.bottom + w * .025f;

    // Tombol RESET
    RectF reset = new RectF(
        w * .37f,
        y,
        w * .63f,
        y + w * .10f
    );

    p.setColor(BG);
    c.drawRoundRect(reset, 20, 20, p);
    c.drawRoundRect(reset, 20, 20, stroke);

    p.setColor(GOLD);
    p.setTextSize(w * .032f);
    c.drawText(
        "RESET",
        reset.centerX(),
        reset.centerY() + p.getTextSize() * .35f,
        p
    );

    return reset.bottom + w * .035f;
        }
    }
    private void drawFooter(Canvas c){float w=getWidth(),h=getHeight();String swap=latin?"GANTI KE TEKS ARAB":"GANTI KE TEKS LATIN";button(c,new RectF(w*.10f,h*.83f,w*.90f,h*.89f),swap,false);button(c,new RectF(w*.07f,h*.91f,w*.47f,h*.975f),"‹  KEMBALI",false);button(c,new RectF(w*.53f,h*.91f,w*.93f,h*.975f),page==12?"SELESAI  ✓":"LANJUT  ›",true);}
    private void button(Canvas c,RectF r,String label,boolean fill){p.setColor(fill?GOLD:BG);c.drawRoundRect(r,18,18,p);c.drawRoundRect(r,18,18,stroke);p.setColor(fill?Color.BLACK:Color.WHITE);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(getWidth()*.04f);p.setTypeface(Typeface.DEFAULT_BOLD);c.drawText(label,r.centerX(),r.centerY()+p.getTextSize()*.35f,p);}

    @Override
public boolean onTouchEvent(android.view.MotionEvent e) {
    if (e.getAction() != MotionEvent.ACTION_UP) return true;

    float x = e.getX();
    float y = e.getY();
    float w = getWidth();
    float h = getHeight();

    // HALAMAN 1
    if (page == 1) {
        page = 2;
        invalidate();
        return true;
    }

    // HALAMAN 2
    if (page == 2) {
        if (x < w * .5f) {
            page = 1;
        } else {
            page = 3;
        }
        invalidate();
        return true;
    }

    // HALAMAN 3
    if (page == 3) {
        if (y > h * .38f && y < h * .78f) {
            latin = y > h * .57f;
            page = 4;
        }

        if (y > h * .82f && y < h * .90f) {
            latin = !latin;
        }

        invalidate();
        return true;
    }

    // HALAMAN DZIKIR 4 - 11
    if (page >= 4 && page <= 11) {

        String k = null;

        if (page == 4) k = "p4a";
        else if (page == 5) k = "p4b";
        else if (page == 6) k = "p5";
        else if (page == 7) k = "p6";
        else if (page == 8) k = "p7";
        else if (page == 9) k = "p8";
        else if (page == 10) k = "p9";
        else if (page == 11) k = "p10";

        // Tombol RESET
        if (reset != null && reset.contains(x, y)) {
            if (k != null) {
                counts.put(k, 0);
            }
            invalidate();
            return true;
        }

        // Tombol HITUNG
        if (hit != null && hit.contains(x, y)) {
            if (k != null) {
                int max = k.equals("p4a") || k.equals("p4b")
                        ? 3
                        : 33;

                int jumlah = counts.containsKey(k)
                        ? counts.get(k)
                        : 0;

                if (jumlah < max) {
                    counts.put(k, jumlah + 1);
                }
            }

            invalidate();
            return true;
        }

        // Tombol KEMBALI
        if (y > h * .90f && x < w * .5f) {
            page = Math.max(3, page - 1);
            invalidate();
            return true;
        }

        // Tombol LANJUT
        if (y > h * .90f && x >= w * .5f) {
            page = page + 1;
            invalidate();
            return true;
        }
    }

    // HALAMAN TERAKHIR
    if (page == 12) {
        ((Activity) getContext()).finish();
        return true;
    }

    return true;
}
    }

    private void drawTextPage(Canvas c,String title,String text,boolean arab){header(c,title);drawPage(c,title,text,null,0,null,null,0);}
    private String arab11(){return "وَالشَيخ سَعِيد الْبُرْهَانِي وَالشَّيخُ هِشَامُ الْبُرْهَانِي\nوالشيخ شُكْري اللُّحُفِي وَالشَيخ سَعْدُ الذِيْن مُرَادُ\nوَالشَّيخ يُوْسُفَ الْبَخور.\nوَإِلَى جَمِيعِ الْمُسْلِمِينَ وَالْمُسْلِمَاتِ وَالْمُؤْمِنِينَ\nوَالْمُؤْمِنَاتِ خُصُوصًا أَبَانَنَا وَأُمَّهَاتِنَا وَأَجْدَادَنَا\nوَجَدَّاتِنَا وَنَخْصُ خُصُوصًا الشَّيخُ زُبَيرُ دَحْلَانُ وَالشَّيخ\nمَيْمُونَ زُبَيرُ وَالشَّيخُ خُضَرِي تكتل رجا وَالشَّيْخ\nدَلْهَارُ واتو جوعول وَالشَّيْخ مُلَا رَمَضَانُ وَالشَّيْخ\nسَعِيْد رَمَضَانُ الْبُوْطِئ وَالشَّيْخ نَعِيمِ الْعَرَقْسُوسِي\nوَالشَّيْخ أَحْمَدُ وَافِى مَيْمُون وَأُمِّهِ مَسْطِيعَةً بِنْتَ إِدْرِيس\nوَالشَّيْخ نَوَاوِي صِدِّيق برجانو\nو الشيخ أبو الفتح\nالميدومي وَالشَّيْخ صَالِحٌ مُفْتِي الْحَنَفِى وَالشَّيْخ إِدْرِيس\nجمسرين صالا وَالشَّيْخ عَبْدُ الْمُعِيد كلاتين";}
}
