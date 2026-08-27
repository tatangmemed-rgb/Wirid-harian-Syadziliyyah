package com.mds.aurad;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class MdsView extends View {

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final SharedPreferences prefs;

    private final Bitmap logo;
    private final Bitmap intro1;
    private final Bitmap intro2;
    private final Bitmap page10Latin;
    private final Bitmap page10Arab;
    private final Bitmap page11Latin;
    private final Bitmap page11Arab;
    private final Bitmap page12Latin;
    private final Bitmap page12Arab;

    private final Map<String, Integer> counts = new HashMap<>();
    private final Map<String, RectF> countButtons = new HashMap<>();
    private final Map<String, RectF> resetButtons = new HashMap<>();

    private int page = 1;
    private boolean latin = false;

    private final int GOLD = Color.rgb(231, 174, 0);
    private final int BG = Color.rgb(5, 5, 5);

    public MdsView(Context c) {
        super(c);

        prefs = c.getSharedPreferences(
                "mds_state",
                Context.MODE_PRIVATE
        );

        logo = load(c, R.drawable.logo_mds);
        intro1 = load(c, R.drawable.intro_1);
        intro2 = load(c, R.drawable.intro_2);

        page10Latin = load(c, R.drawable.page10_latin);
        page10Arab = load(c, R.drawable.page10_arab);

        page11Latin = load(c, R.drawable.page11_latin);
        page11Arab = load(c, R.drawable.page11_arab);

        page12Latin = load(c, R.drawable.page12_latin);
        page12Arab = load(c, R.drawable.page12_arab);

        String[] keys = {
                "p4a",
                "p4b",
                "p5",
                "p7",
                "p8",
                "p9"
        };

        for (String k : keys) {
            counts.put(k, prefs.getInt(k, 0));
        }

        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(3f);
        stroke.setColor(GOLD);

        setFocusable(true);
    }


    private Bitmap load(Context c, int id) {
        return BitmapFactory.decodeResource(
                c.getResources(),
                id
        );
    }


    @Override
    protected void onDraw(Canvas c) {

        super.onDraw(c);

        c.drawColor(BG);

        countButtons.clear();
        resetButtons.clear();


        // HALAMAN 1
        if (page == 1) {
            drawFull(c, intro1);
            return;
        }


        // HALAMAN 2
        if (page == 2) {
            drawFull(c, intro2);
            return;
        }


        // HALAMAN 3
        if (page == 3) {
            drawSelect(c);
            return;
        }


        /*
         * HALAMAN 10
         *
         * Bagian bawah gambar dipotong agar angka 10/22
         * dan tombol lama tidak ikut tampil.
         */
        if (page == 10) {
            drawImagePage(
                    c,
                    latin ? page10Latin : page10Arab,
                    0.88f
            );
            return;
        }


        /*
         * HALAMAN 11
         *
         * MEMAKAI GAMBAR ASLI YANG DIUPLOAD.
         *
         * Bagian bawah gambar yang berisi tombol lama
         * dipotong/dihilangkan.
         *
         * Tombol baru dibuat oleh aplikasi, sama seperti
         * halaman 10.
         */
        if (page == 11) {
            drawImagePage(
                    c,
                    latin ? page11Latin : page11Arab,
                    0.79f
            );
            return;
        }


        /*
         * HALAMAN 12
         *
         * MEMAKAI GAMBAR ASLI.
         * Tombol lama yang tercetak pada gambar dipotong.
         * Hanya tombol aplikasi yang aktif.
         */
        if (page == 12) {
            drawImagePage(
                    c,
                    latin ? page12Latin : page12Arab,
                    0.79f
            );
            return;
        }


        // HALAMAN 4 - 9
        drawContent(c);
    }


    /*
     * Menampilkan gambar penuh.
     */
    private void drawFull(Canvas c, Bitmap b) {

        if (b == null) {
            return;
        }

        c.drawBitmap(
                b,
                new Rect(
                        0,
                        0,
                        b.getWidth(),
                        b.getHeight()
                ),
                new RectF(
                        0,
                        0,
                        getWidth(),
                        getHeight()
                ),
                p
        );
    }


    /*
     * Menampilkan bagian atas gambar saja.
     *
     * sourceBottom menentukan berapa bagian gambar
     * yang dipakai.
     *
     * Bagian bawah gambar yang berisi tombol lama
     * tidak digambar.
     */
    private void drawImagePage(
            Canvas c,
            Bitmap b,
            float sourceBottom
    ) {

        float w = getWidth();
        float h = getHeight();

        c.drawColor(BG);

        if (b != null) {

            int srcBottom = Math.max(
                    1,
                    Math.min(
                            b.getHeight(),
                            Math.round(
                                    b.getHeight() * sourceBottom
                            )
                    )
            );

            Rect src = new Rect(
                    0,
                    0,
                    b.getWidth(),
                    srcBottom
            );

            RectF dst = new RectF(
                    0,
                    0,
                    w,
                    h * 0.79f
            );

            c.drawBitmap(
                    b,
                    src,
                    dst,
                    p
            );
        }

        drawFooter(c);
    }


    /*
     * Header halaman 3 - 9.
     */
    private void header(
            Canvas c,
            String title
    ) {

        float w = getWidth();
        float h = getHeight();

        c.drawRoundRect(
                new RectF(
                        12,
                        12,
                        w - 12,
                        h - 12
                ),
                24,
                24,
                stroke
        );

        float size = Math.min(
                w * 0.22f,
                h * 0.16f
        );

        if (logo != null) {

            c.drawBitmap(
                    logo,
                    null,
                    new RectF(
                            (w - size) / 2f,
                            25,
                            (w + size) / 2f,
                            25 + size
                    ),
                    p
            );
        }

        p.setColor(GOLD);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(
                Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                )
        );

        p.setTextSize(w * 0.055f);

        if (!title.isEmpty()) {

            c.drawText(
                    title,
                    w / 2f,
                    25 + size + 52,
                    p
            );
        }

        c.drawLine(
                w * 0.12f,
                25 + size + 75,
                w * 0.88f,
                25 + size + 75,
                p
        );
    }


    /*
     * HALAMAN PILIH TEKS
     */
    private void drawSelect(Canvas c) {

        float w = getWidth();
        float h = getHeight();

        header(c, "PILIH TEKS");

        button(
                c,
                new RectF(
                        w * 0.10f,
                        h * 0.42f,
                        w * 0.90f,
                        h * 0.56f
                ),
                "عَرَبِيّ   TEKS ARAB",
                true
        );

        button(
                c,
                new RectF(
                        w * 0.10f,
                        h * 0.60f,
                        w * 0.90f,
                        h * 0.74f
                ),
                "Aa   TEKS LATIN",
                false
        );
    }


    /*
     * ISI HALAMAN 4 - 9
     */
    private void drawContent(Canvas c) {

        String title = "";
        String first = "";
        String key = null;
        String second = null;
        String key2 = null;

        int target = 0;
        int target2 = 0;


        // HALAMAN 4
        if (page == 4) {

            title = latin
                    ? "WIRID THORIQOH SYADZILIYYAH"
                    : "وِرْدُ الطَّرِيقَةِ الشَّاذِلِيَّةِ";

            first = latin
                    ? "AUDZUBILLAHIMNASSYAITHONIRROJIIM (3X)"
                    : "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ (٣×)";

            key = "p4a";
            target = 3;

            second = latin
                    ? "BISMILLAAHIRROHMAANIRROHIIM (3X)"
                    : "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ (٣×)";

            key2 = "p4b";
            target2 = 3;
        }


        // HALAMAN 5
        else if (page == 5) {

            first = latin
                    ? "WAMAA TUQODDIMUU LI ANFUSIKUM MIN KHOIRIN TAJIDUUHU 'INDALLOOHI HUWA KHOIRON WA A'ZHOMA AJRON WASTAGHFIRULLOOH. INNALLOOHA GHOFUURUR ROHIIM."
                    : "وَمَا تُقَدِّمُوا لِأَنْفُسِكُمْ مِنْ خَيْرٍ تَجِدُوهُ عِنْدَ اللَّهِ هُوَ خَيْرًا وَأَعْظَمُ أَجْرًا وَاسْتَغْفِرُوا اللَّهَ إِنَّ اللَّهَ غَفُورٌ رَحِيمٌ.";

            second = latin
                    ? "ASTAGHFIRULLAAHAL 'AZHIIM (99X)"
                    : "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ (٩٩×)";

            key2 = "p5";
            target2 = 99;
        }


        // HALAMAN 6
        else if (page == 6) {

            first = latin
                    ? "ASTAGHFIRULLAAHAL 'AZHIIM. ALLADZII LAA ILAAHA ILLAA HUWAL HAYYUL QOYYUUMU WA ATUUBU ILAIH"
                    : "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ الَّذِي لَا إِلٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ";

            second = latin
                    ? "INNALLAAHA WA MALAAIKATAHUU YUSHOLLUUNA 'ALAN NABIYYI. YAA AYYUHALLADZIINA AAMANUU SHOLLUU 'ALAIHI WA SALLIMUU TASLIIMAA."
                    : "إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ يَا أَيُّهَا الَّذِينَ آمَنُوا صَلُّوا عَلَيْهِ وَسَلِّمُوا تَسْلِيمًا.";
        }


        // HALAMAN 7
        else if (page == 7) {

            first = latin
                    ? "ALLAAHUMMA SHOLLI 'ALAA SAYYIDINAA MUHAMMADIN 'ABDIKA WA ROSUULIKAN NABIYYIL UMMIYYI WA 'ALAA AALIHI WA SHOHBIHI WA SALLIM (99X)"
                    : "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ عَبْدِكَ وَرَسُولِكَ النَّبِيِّ الْأُمِّيِّ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلِّمْ (٩٩×)";

            key = "p7";
            target = 99;

            second = latin
                    ? "ALLAAHUMMA SHOLLI 'ALAA SAYYIDINAA MUHAMMADIN 'ABDIKA WA ROSUULIKAN NABIYYIL UMMIYYI WA 'ALAA AALIHI WA SHOHBIHI WA SALLIM TASLIIMAN BIQODRI 'AZHOMATI DZAATIKA FII KULLI WAQTIN WA HIIN."
                    : "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ عَبْدِكَ وَرَسُولِكَ النَّبِيِّ الْأُمِّيِّ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلِّمْ تَسْلِيمًا بِقَدْرِ عَظَمَةِ ذَاتِكَ فِي كُلِّ وَقْتٍ وَحِينٍ.";
        }


        // HALAMAN 8
        else if (page == 8) {

            title = latin
                    ? "FA'LAM ANNAHU"
                    : "فَاعْلَمْ أَنَّهُ";

            first = latin
                    ? "LAA ILAAHA ILLALLAAH (99X)"
                    : "لَا إِلٰهَ إِلَّا اللَّهُ (٩٩×)";

            key = "p8";
            target = 99;

            second = latin
                    ? "LAA ILAAHA ILLALLAAHU MUHAMMADUR ROSUULULLAAHI SHOLLALLAAHU 'ALAIHI WA 'ALAA AALIHI WA SHOHBIHI WA SALLAM."
                    : "لَا إِلٰهَ إِلَّا اللَّهُ مُحَمَّدٌ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلَّمَ.";
        }


        // HALAMAN 9
        else if (page == 9) {

            first = latin
                    ? "BISMILLAAHIRROHMAANIRROHIIM.\nQUL HUWALLOOHU AHAD.\nALLOOHUSH SHOMAD.\nLAM YALID WA LAM YUULAD WA LAM YAKUN LAHUU KUFUWAN AHAD (3X)"
                    : "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ.\nقُلْ هُوَ اللَّهُ أَحَدٌ.\nاللَّهُ الصَّمَدُ.\nلَمْ يَلِدْ وَلَمْ يُولَدْ وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ (٣×)";

            key = "p9";
            target = 3;
        }


        drawPage(
                c,
                title,
                first,
                key,
                target,
                second,
                key2,
                target2
        );
    }


    /*
     * Menggambar halaman teks.
     */
    private void drawPage(
            Canvas c,
            String title,
            String first,
            String key,
            int target,
            String second,
            String key2,
            int target2
    ) {

        float w = getWidth();
        float h = getHeight();

        header(c, title);

        float top = h * 0.23f;

        float firstSize = latin
                ? 40f
                : 58f;

        float secondSize = latin
                ? 38f
                : 54f;


        if (!first.isEmpty()) {

            top = drawParagraph(
                    c,
                    first,
                    w * 0.05f,
                    w * 0.95f,
                    top,
                    firstSize
            );

            if (key != null) {

                top = drawCounter(
                        c,
                        key,
                        target,
                        top + 16f
                );
            }
        }


        if (second != null) {

            top = drawParagraph(
                    c,
                    second,
                    w * 0.05f,
                    w * 0.95f,
                    top + 24f,
                    secondSize
            );

            if (key2 != null) {

                drawCounter(
                        c,
                        key2,
                        target2,
                        top + 16f
                );
            }
        }

        drawFooter(c);
    }


    private float drawParagraph(
            Canvas c,
            String text,
            float left,
            float right,
            float y,
            float size
    ) {

        return drawParagraphMode(
                c,
                text,
                left,
                right,
                y,
                size,
                !latin
        );
    }


    private float drawParagraphMode(
            Canvas c,
            String text,
            float left,
            float right,
            float y,
            float size,
            boolean arabic
    ) {

        p.setColor(Color.WHITE);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(size);

        if (arabic) {
            p.setTypeface(
                    Typeface.create(
                            "serif",
                            Typeface.NORMAL
                    )
            );
        } else {
            p.setTypeface(
                    Typeface.create(
                            Typeface.SANS_SERIF,
                            Typeface.BOLD
                    )
            );
        }

        String[] lines = text.split("\n");

        for (String line : lines) {

            c.drawText(
                    line,
                    (left + right) / 2f,
                    y,
                    p
            );

            y += size * (
                    arabic
                            ? 1.22f
                            : 1.35f
            );
        }

        return y;
    }


    /*
     * TOMBOL HITUNG DAN RESET
     */
    private float drawCounter(
            Canvas c,
            String key,
            int target,
            float y
    ) {

        float w = getWidth();

        int n = counts.containsKey(key)
                ? counts.get(key)
                : 0;

        p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setColor(GOLD);
        p.setTextSize(w * 0.055f);

        c.drawText(
                n + " / " + target,
                w / 2f,
                y,
                p
        );


        y += w * 0.025f;


        RectF hit = new RectF(
                w * 0.22f,
                y,
                w * 0.78f,
                y + w * 0.19f
        );

        countButtons.put(
                key,
                new RectF(hit)
        );

        c.drawOval(hit, stroke);

        p.setColor(Color.WHITE);
        p.setTextSize(w * 0.055f);

        c.drawText(
                "HITUNG",
                w / 2f,
                hit.centerY()
                        + p.getTextSize() * 0.35f,
                p
        );


        y = hit.bottom + w * 0.025f;


        RectF reset = new RectF(
                w * 0.37f,
                y,
                w * 0.63f,
                y + w * 0.10f
        );

        resetButtons.put(
                key,
                new RectF(reset)
        );


        p.setColor(BG);

        c.drawRoundRect(
                reset,
                20,
                20,
                p
        );

        c.drawRoundRect(
                reset,
                20,
                20,
                stroke
        );


        p.setColor(GOLD);
        p.setTextSize(w * 0.032f);

        c.drawText(
                "RESET",
                reset.centerX(),
                reset.centerY()
                        + p.getTextSize() * 0.35f,
                p
        );

        return reset.bottom + w * 0.035f;
    }


    /*
     * FOOTER UNTUK HALAMAN 4 - 12
     *
     * Termasuk halaman 10, 11 dan 12.
     */
    private void drawFooter(Canvas c) {

        float w = getWidth();
        float h = getHeight();


        // GANTI TEKS
        button(
                c,
                new RectF(
                        w * 0.07f,
                        h * 0.80f,
                        w * 0.93f,
                        h * 0.885f
                ),
                latin
                        ? "GANTI KE TEKS ARAB"
                        : "GANTI KE TEKS LATIN",
                false
        );


        // KEMBALI
        button(
                c,
                new RectF(
                        w * 0.07f,
                        h * 0.905f,
                        w * 0.47f,
                        h * 0.985f
                ),
                "‹  KEMBALI",
                false
        );


        // LANJUT / SELESAI
        button(
                c,
                new RectF(
                        w * 0.53f,
                        h * 0.905f,
                        w * 0.93f,
                        h * 0.985f
                ),
                page == 12
                        ? "SELESAI  ✓"
                        : "LANJUT  ›",
                true
        );
    }


    private void button(
            Canvas c,
            RectF r,
            String label,
            boolean fill
    ) {

        p.setColor(
                fill
                        ? GOLD
                        : BG
        );

        c.drawRoundRect(
                r,
                18,
                18,
                p
        );

        c.drawRoundRect(
                r,
                18,
                18,
                stroke
        );


        p.setColor(
                fill
                        ? Color.BLACK
                        : Color.WHITE
        );

        p.setTextAlign(Paint.Align.CENTER);

        p.setTextSize(
                getWidth() * 0.04f
        );

        p.setTypeface(Typeface.DEFAULT_BOLD);

        c.drawText(
                label,
                r.centerX(),
                r.centerY()
                        + p.getTextSize() * 0.35f,
                p
        );
    }


    /*
     * SEMUA FUNGSI TOMBOL
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (e.getAction()
                != MotionEvent.ACTION_UP) {
            return true;
        }


        float x = e.getX();
        float y = e.getY();

        float w = getWidth();
        float h = getHeight();


        // HALAMAN 1 -> 2
        if (page == 1) {

            page = 2;
            invalidate();

            return true;
        }


        // HALAMAN 2 -> 3
        if (page == 2) {

            page = 3;
            invalidate();

            return true;
        }


        // PILIH TEKS
        if (page == 3) {

            if (
                    y >= h * 0.42f
                            && y <= h * 0.56f
            ) {

                latin = false;
                page = 4;

            } else if (
                    y >= h * 0.60f
                            && y <= h * 0.74f
            ) {

                latin = true;
                page = 4;
            }

            invalidate();

            return true;
        }


        // HALAMAN 4 - 12
        if (
                page >= 4
                        && page <= 12
        ) {


            /*
             * RESET DAN HITUNG DIPERIKSA DAHULU.
             *
             * Ini penting agar tombol halaman 5
             * tidak tertukar antara HITUNG dan RESET.
             */
            if (handleCounterTouch(x, y)) {

                invalidate();

                return true;
            }


            // GANTI TEKS
            if (
                    x >= w * 0.07f
                            && x <= w * 0.93f
                            && y >= h * 0.80f
                            && y <= h * 0.885f
            ) {

                latin = !latin;

                invalidate();

                return true;
            }


            // KEMBALI
            if (
                    x >= w * 0.07f
                            && x <= w * 0.47f
                            && y >= h * 0.905f
                            && y <= h * 0.985f
            ) {

                page = Math.max(
                        3,
                        page - 1
                );

                invalidate();

                return true;
            }


            // LANJUT / SELESAI
            if (
                    x >= w * 0.53f
                            && x <= w * 0.93f
                            && y >= h * 0.905f
                            && y <= h * 0.985f
            ) {

                if (page == 12) {

                    if (
                            getContext()
                                    instanceof Activity
                    ) {

                        ((Activity) getContext()).finish();
                    }

                } else {

                    page++;

                    invalidate();
                }

                return true;
            }
        }


        return true;
    }


    /*
     * MENANGANI HITUNG DAN RESET
     */
    private boolean handleCounterTouch(
            float x,
            float y
    ) {


        /*
         * RESET DICEK LEBIH DULU.
         *
         * RESET benar-benar mengembalikan hitungan
         * menjadi 0.
         */
        for (
                Map.Entry<String, RectF> entry
                        : resetButtons.entrySet()
        ) {

            if (
                    entry.getValue().contains(x, y)
            ) {

                String key = entry.getKey();

                counts.put(
                        key,
                        0
                );

                prefs.edit()
                        .putInt(
                                key,
                                0
                        )
                        .apply();

                return true;
            }
        }


        /*
         * HITUNG
         */
        for (
                Map.Entry<String, RectF> entry
                        : countButtons.entrySet()
        ) {

            if (
                    entry.getValue().contains(x, y)
            ) {

                String key = entry.getKey();

                int max = targetFor(key);

                int value = counts.containsKey(key)
                        ? counts.get(key)
                        : 0;


                if (value < max) {

                    value++;
                }


                counts.put(
                        key,
                        value
                );


                prefs.edit()
                        .putInt(
                                key,
                                value
                        )
                        .apply();

                return true;
            }
        }


        return false;
    }


    /*
     * TARGET MASING-MASING DZIKIR
     */
    private int targetFor(String key) {

        if (
                "p4a".equals(key)
                        || "p4b".equals(key)
                        || "p9".equals(key)
        ) {

            return 3;
        }


        if (
                "p5".equals(key)
                        || "p7".equals(key)
                        || "p8".equals(key)
        ) {

            return 99;
        }


        return Integer.MAX_VALUE;
    }
    }
