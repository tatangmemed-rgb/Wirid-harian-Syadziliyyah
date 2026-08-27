package com.mds.aurad;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import java.util.*;

public class MdsView extends View {

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final SharedPreferences prefs;
    private final Bitmap logo, intro1, intro2, page10Latin, page11Latin, page11Arab, page12Latin, page10Arab, page12Arab;
    private final Map<String, Integer> counts = new HashMap<>();
    private final Map<String, RectF> countButtons = new HashMap<>();
    private final Map<String, RectF> resetButtons = new HashMap<>();

    private int page = 1;
    private boolean latin = false;

    private final int GOLD = Color.rgb(231, 174, 0);
    private final int BG = Color.rgb(5, 5, 5);

    public MdsView(Context c) {
        super(c);
        prefs = c.getSharedPreferences("mds_state", Context.MODE_PRIVATE);

        logo = load(c, R.drawable.logo_mds);
        intro1 = load(c, R.drawable.intro_1);
        intro2 = load(c, R.drawable.intro_2);
        page10Latin = load(c, R.drawable.page10_latin);
        page11Latin = load(c, R.drawable.page11_latin);
        page11Arab = load(c, R.drawable.page11_arab);
        page12Latin = load(c, R.drawable.page12_latin);
        page10Arab = load(c, R.drawable.page10_arab);
        page12Arab = load(c, R.drawable.page12_arab);

        String[] keys = {"p4a", "p4b", "p5", "p7", "p8", "p9"};
        for (String k : keys) counts.put(k, prefs.getInt(k, 0));

        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(3f);
        stroke.setColor(GOLD);
    }

    private Bitmap load(Context c, int id) {
        return BitmapFactory.decodeResource(c.getResources(), id);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawColor(BG);
        countButtons.clear();
        resetButtons.clear();

        if (page == 1) { drawFull(c, intro1); return; }
        if (page == 2) { drawFull(c, intro2); return; }
        if (page == 3) { drawSelect(c); return; }

        // HALAMAN 10: gunakan gambar teks halaman 10 secara utuh.
        // Tombol tidak diambil dari gambar; tombol aktif aplikasi tetap digambar
        // oleh drawFooter(), sama seperti halaman sebelum dan sesudahnya.
        if (page == 10) {
            drawImagePage(c, latin ? page10Latin : page10Arab, 1.00f);
            return;
        }

        // HALAMAN 11: gambar asli Arab/Latin dipakai.
        // Hanya bagian paling bawah tempat tombol lama berada yang disisihkan.
        // Tidak lagi memakai pemotongan kasar 79% yang dapat memotong teks.
        if (page == 11) {
            drawImagePage(c, latin ? page11Latin : page11Arab, .90f);
            return;
        }

        // HALAMAN 12: tombol lama yang tercetak di gambar dipotong/dihilangkan.
        // Hanya tombol baru aplikasi yang aktif yang digambar.
        if (page == 12) {
            drawImagePage(c, latin ? page12Latin : page12Arab, .90f);
            return;
        }

        drawContent(c);
    }

    private void drawFull(Canvas c, Bitmap b) {
        if (b == null) return;
        c.drawBitmap(b, new Rect(0, 0, b.getWidth(), b.getHeight()),
                new RectF(0, 0, getWidth(), getHeight()), p);
    }

    // Menampilkan bagian atas gambar dan menyisakan ruang untuk tombol aplikasi.
    // sourceBottom adalah persentase gambar sumber yang dipakai; bagian bawah
    // yang berisi tombol lama tidak ikut digambar.
    private void drawImagePage(Canvas c, Bitmap b, float sourceBottom) {
        float w = getWidth(), h = getHeight();
        c.drawColor(BG);

        if (b != null) {
            int srcBottom = Math.max(1, Math.min(b.getHeight(),
                    Math.round(b.getHeight() * sourceBottom)));
            Rect src = new Rect(0, 0, b.getWidth(), srcBottom);
            RectF dst = new RectF(0, 0, w, h * .79f);
            c.drawBitmap(b, src, dst, p);
        }

        drawFooter(c);
    }

    private void header(Canvas c, String title) {
        float w = getWidth(), h = getHeight();
        c.drawRoundRect(new RectF(12, 12, w - 12, h - 12), 24, 24, stroke);

        float size = Math.min(w * .22f, h * .16f);
        if (logo != null) {
            c.drawBitmap(logo, null,
                    new RectF((w - size) / 2f, 25, (w + size) / 2f, 25 + size), p);
        }

        p.setColor(GOLD);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        p.setTextSize(w * .055f);

        if (!title.isEmpty()) c.drawText(title, w / 2f, 25 + size + 52, p);

        c.drawLine(w * .12f, 25 + size + 75, w * .88f, 25 + size + 75, p);
    }

    private void drawSelect(Canvas c) {
        float w = getWidth(), h = getHeight();
        header(c, "PILIH TEKS");
        button(c, new RectF(w * .10f, h * .42f, w * .90f, h * .56f),
                "عَرَبِيّ   TEKS ARAB", true);
        button(c, new RectF(w * .10f, h * .60f, w * .90f, h * .74f),
                "Aa   TEKS LATIN", false);
    }

    private void drawContent(Canvas c) {
        String title = "", first = "", key = null, second = null, key2 = null;
        int target = 0, target2 = 0;

        if (page == 4) {
            title = latin ? "WIRID THORIQOH SYADZILIYYAH" : "وِرْدُ الطَّرِيقَةِ الشَّاذِلِيَّةِ";
            first = latin ? "AUDZUBILLAHIMNASSYAITHONIRROJIIM (3X)"
                    : "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ (٣×)";
            key = "p4a"; target = 3;
            second = latin ? "BISMILLAAHIRROHMAANIRROHIIM (3X)"
                    : "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ (٣×)";
            key2 = "p4b"; target2 = 3;
        } else if (page == 5) {
            first = latin
                    ? "WAMAA TUQODDIMUU LI ANFUSIKUM MIN KHOIRIN TAJIDUUHU 'INDALLOOHI HUWA KHOIRON WA A'ZHOMA AJRON WASTAGHFIRULLOOH. INNALLOOHA GHOFUURUR ROHIIM."
                    : "وَمَا تُقَدِّمُوا لِأَنْفُسِكُمْ مِنْ خَيْرٍ تَجِدُوهُ عِنْدَ اللَّهِ هُوَ خَيْرًا وَأَعْظَمُ أَجْرًا وَاسْتَغْفِرُوا اللَّهَ إِنَّ اللَّهَ غَفُورٌ رَحِيمٌ.";
            second = latin ? "ASTAGHFIRULLAAHAL 'AZHIIM (99X)"
                    : "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ (٩٩×)";
            key2 = "p5"; target2 = 99;
        } else if (page == 6) {
            first = latin
                    ? "ASTAGHFIRULLAAHAL 'AZHIIM. ALLADZII LAA ILAAHA ILLAA HUWAL HAYYUL QOYYUUMU WA ATUUBU ILAIH"
                    : "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ الَّذِي لَا إِلٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ";
            second = latin
                    ? "INNALLAAHA WA MALAAIKATAHUU YUSHOLLUUNA 'ALAN NABIYYI. YAA AYYUHALLADZIINA AAMANUU SHOLLUU 'ALAIHI WA SALLIMUU TASLIIMAA."
                    : "إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ يَا أَيُّهَا الَّذِينَ آمَنُوا صَلُّوا عَلَيْهِ وَسَلِّمُوا تَسْلِيمًا.";
        } else if (page == 7) {
            first = latin
                    ? "ALLAAHUMMA SHOLLI 'ALAA SAYYIDINAA MUHAMMADIN 'ABDIKA WA ROSUULIKAN NABIYYIL UMMIYYI WA 'ALAA AALIHI WA SHOHBIHI WA SALLIM (99X)"
                    : "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ عَبْدِكَ وَرَسُولِكَ النَّبِيِّ الْأُمِّيِّ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلِّمْ (٩٩×)";
            key = "p7"; target = 99;
            second = latin
                    ? "ALLAAHUMMA SHOLLI 'ALAA SAYYIDINAA MUHAMMADIN 'ABDIKA WA ROSUULIKAN NABIYYIL UMMIYYI WA 'ALAA AALIHI WA SHOHBIHI WA SALLIM TASLIIMAN BIQODRI 'AZHOMATI DZAATIKA FII KULLI WAQTIN WA HIIN."
                    : "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ عَبْدِكَ وَرَسُولِكَ النَّبِيِّ الْأُمِّيِّ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلِّمْ تَسْلِيمًا بِقَدْرِ عَظَمَةِ ذَاتِكَ فِي كُلِّ وَقْتٍ وَحِينٍ.";
        } else if (page == 8) {
            title = latin ? "FA'LAM ANNAHU" : "فَاعْلَمْ أَنَّهُ";
            first = latin ? "LAA ILAAHA ILLALLAAH (99X)"
                    : "لَا إِلٰهَ إِلَّا اللَّهُ (٩٩×)";
            key = "p8"; target = 99;
            second = latin
                    ? "LAA ILAAHA ILLALLAAHU MUHAMMADUR ROSUULULLAAHI SHOLLALLAAHU 'ALAIHI WA 'ALAA AALIHI WA SHOHBIHI WA SALLAM."
                    : "لَا إِلٰهَ إِلَّا اللَّهُ مُحَمَّدٌ رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَعَلَى آلِهِ وَصَحْبِهِ وَسَلَّمَ.";
        } else if (page == 9) {
            first = latin
                    ? "BISMILLAAHIRROHMAANIRROHIIM.\nQUL HUWALLOOHU AHAD.\nALLOOHUSH SHOMAD.\nLAM YALID WA LAM YUULAD WA LAM YAKUN LAHUU KUFUWAN AHAD (3X)"
                    : "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ.\nقُلْ هُوَ اللَّهُ أَحَدٌ.\nاللَّهُ الصَّمَدُ.\nلَمْ يَلِدْ وَلَمْ يُولَدْ وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ (٣×)";
            key = "p9"; target = 3;
        }

        drawPage(c, title, first, key, target, second, key2, target2);
    }

    private void drawPage(Canvas c, String title, String first, String key, int target,
                          String second, String key2, int target2) {
        float w = getWidth(), h = getHeight();
        header(c, title);
        float left = w * .05f, right = w * .95f;
        float topStart = h * .23f, bottomLimit = h * .785f;

        // Font mulai sedikit lebih besar; teks panjang otomatis turun ke baris bawah.
        float firstSize = latin ? 44f : 62f;
        float secondSize = latin ? 42f : 58f;
        float minFirst = latin ? 36f : 50f;
        float minSecond = latin ? 34f : 48f;

        while (true) {
            float need = estimateParagraphHeight(first, right-left, firstSize, !latin);
            if (key != null) need += estimateCounterHeight(w);
            if (second != null) {
                need += 24f + estimateParagraphHeight(second, right-left, secondSize, !latin);
                if (key2 != null) need += estimateCounterHeight(w);
            }
            if (topStart + need <= bottomLimit) break;
            boolean changed = false;
            if (firstSize > minFirst) { firstSize -= 2f; changed = true; }
            if (second != null && secondSize > minSecond) { secondSize -= 2f; changed = true; }
            if (!changed) break;
        }

        float top = topStart;
        if (!first.isEmpty()) {
            top = drawParagraph(c, first, left, right, top, firstSize);
            if (key != null) top = drawCounter(c, key, target, top + 16f);
        }
        if (second != null) {
            top = drawParagraph(c, second, left, right, top + 24f, secondSize);
            if (key2 != null) drawCounter(c, key2, target2, top + 16f);
        }
        drawFooter(c);
    }

    private float estimateParagraphHeight(String text, float width, float size, boolean arabic) {
        if (text == null || text.isEmpty()) return 0f;
        p.setTextSize(size);
        p.setTypeface(arabic ? Typeface.create("serif", Typeface.NORMAL)
                : Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        int lines = 0;
        String[] parts = text.split("\\n");
        for (String raw : parts) lines += wrap(raw, width, p).size();
        return lines * size * (arabic ? 1.22f : 1.35f);
    }

    private float estimateCounterHeight(float w) {
        return w*.055f + w*.025f + w*.19f + w*.025f + w*.10f + w*.035f;
    }

    private void drawTextPage(Canvas c, String title, String text, boolean arabic) {
        header(c, title);
        drawParagraphMode(c, text, getWidth() * .08f, getWidth() * .92f,
                getHeight() * .24f, arabic ? 44f : 38f, arabic);
        drawFooter(c);
    }

    private float drawParagraph(Canvas c, String text, float left, float right, float y, float size) {
        return drawParagraphMode(c, text, left, right, y, size, !latin);
    }

    private float drawParagraphMode(Canvas c, String text, float left, float right,
                                    float y, float size, boolean arabic) {
        p.setColor(Color.WHITE);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(size);
        p.setTypeface(arabic ? Typeface.create("serif", Typeface.NORMAL)
                : Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));

        String[] lines = text.split("\n");
        for (String raw : lines) {
            for (String line : wrap(raw, right - left, p)) {
                c.drawText(line, (left + right) / 2f, y, p);
                y += size * (arabic ? 1.22f : 1.35f);
            }
        }
        return y;
    }

    private List<String> wrap(String text, float width, Paint paint) {
        ArrayList<String> out = new ArrayList<>();
        String current = "";
        for (String word : text.trim().split(" ")) {
            String next = current.isEmpty() ? word : current + " " + word;
            if (paint.measureText(next) > width && !current.isEmpty()) {
                out.add(current);
                current = word;
            } else {
                current = next;
            }
        }
        if (!current.isEmpty()) out.add(current);
        return out;
    }

    private float drawCounter(Canvas c, String key, int target, float y) {
        float w = getWidth();
        int n = counts.containsKey(key) ? counts.get(key) : 0;

        p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setColor(GOLD);
        p.setTextSize(w * .055f);
        c.drawText(n + " / " + target, w / 2f, y, p);

        y += w * .025f;
        RectF hit = new RectF(w * .22f, y, w * .78f, y + w * .19f);
        countButtons.put(key, new RectF(hit));
        c.drawOval(hit, stroke);

        p.setColor(Color.WHITE);
        p.setTextSize(w * .055f);
        c.drawText("HITUNG", w / 2f, hit.centerY() + p.getTextSize() * .35f, p);

        y = hit.bottom + w * .025f;
        RectF reset = new RectF(w * .37f, y, w * .63f, y + w * .10f);
        resetButtons.put(key, new RectF(reset));

        p.setColor(BG);
        c.drawRoundRect(reset, 20, 20, p);
        c.drawRoundRect(reset, 20, 20, stroke);

        p.setColor(GOLD);
        p.setTextSize(w * .032f);
        c.drawText("RESET", reset.centerX(),
                reset.centerY() + p.getTextSize() * .35f, p);

        return reset.bottom + w * .035f;
    }

    private void drawFooter(Canvas c) {
        float w = getWidth(), h = getHeight();

        // Tombol ganti teks dibuat lebih tinggi agar mudah dipakai di HP.
        button(c, new RectF(w * .07f, h * .80f, w * .93f, h * .885f),
                latin ? "GANTI KE TEKS ARAB" : "GANTI KE TEKS LATIN", false);

        button(c, new RectF(w * .07f, h * .905f, w * .47f, h * .985f),
                "‹  KEMBALI", false);
        button(c, new RectF(w * .53f, h * .905f, w * .93f, h * .985f),
                page == 12 ? "SELESAI  ✓" : "LANJUT  ›", true);
    }

    private void button(Canvas c, RectF r, String label, boolean fill) {
        p.setColor(fill ? GOLD : BG);
        c.drawRoundRect(r, 18, 18, p);
        c.drawRoundRect(r, 18, 18, stroke);
        p.setColor(fill ? Color.BLACK : Color.WHITE);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(getWidth() * .04f);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        c.drawText(label, r.centerX(), r.centerY() + p.getTextSize() * .35f, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() != MotionEvent.ACTION_UP) return true;

        float x = e.getX(), y = e.getY();
        float w = getWidth(), h = getHeight();

        if (page == 1) {
            page = 2; invalidate(); return true;
        }
        if (page == 2) {
            page = 3; invalidate(); return true;
        }
        if (page == 3) {
            if (y >= h * .42f && y <= h * .56f) {
                latin = false; page = 4;
            } else if (y >= h * .60f && y <= h * .74f) {
                latin = true; page = 4;
            }
            invalidate();
            return true;
        }

        if (page >= 4 && page <= 12) {
            // Tombol GANTI TEKS: cek X dan Y agar hanya tombolnya yang memicu.
            if (x >= w * .07f && x <= w * .93f && y >= h * .80f && y <= h * .885f) {
                latin = !latin;
                invalidate();
                return true;
            }

            if (y >= h * .905f && x >= w * .07f && x <= w * .47f) {
                page = Math.max(3, page - 1);
                invalidate();
                return true;
            }

            if (y >= h * .905f && x >= w * .53f && x <= w * .93f) {
                if (page == 12) {
                    if (getContext() instanceof Activity) ((Activity) getContext()).finish();
                } else {
                    page++;
                    invalidate();
                }
                return true;
            }

            if (handleCounterTouch(x, y, w, h)) {
                invalidate();
                return true;
            }
        }
        return true;
    }

    private boolean handleCounterTouch(float x, float y, float w, float h) {
        // RESET harus diperiksa lebih dulu. Tombol ini selalu mengembalikan
        // hitungan dzikir yang sesuai menjadi 0 dan menyimpannya.
        for (Map.Entry<String, RectF> entry : resetButtons.entrySet()) {
            if (entry.getValue().contains(x, y)) {
                String key = entry.getKey();
                counts.put(key, 0);
                prefs.edit().putInt(key, 0).apply();
                return true;
            }
        }

        // HITUNG hanya aktif bila yang disentuh benar-benar area tombol HITUNG.
        for (Map.Entry<String, RectF> entry : countButtons.entrySet()) {
            if (entry.getValue().contains(x, y)) {
                String key = entry.getKey();
                int max = targetFor(key);
                int value = counts.containsKey(key) ? counts.get(key) : 0;
                if (value < max) value++;
                counts.put(key, value);
                prefs.edit().putInt(key, value).apply();
                return true;
            }
        }

        return false;
    }

    private int targetFor(String key) {
        if ("p4a".equals(key) || "p4b".equals(key) || "p9".equals(key)) return 3;
        if ("p5".equals(key) || "p7".equals(key) || "p8".equals(key)) return 99;
        return Integer.MAX_VALUE;
    }

}
