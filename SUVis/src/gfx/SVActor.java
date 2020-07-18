/*
 * SVActor.java
 *  
 * Project: BotoSeis
 *
 * Federal University of Para, Brazil.
 * Department of Geophysics
 * 
 */
package gfx;

import javax.swing.JOptionPane;

/**
 * The class SVActor represents an independent object capable of drawing itself
 * on a graphics context supplied by the user.
 *
 * @author Williams Lima
 */
abstract public class SVActor {

    /**
     * Called each time we want it to be redrawed.
     */
    abstract public void paint(java.awt.Graphics g);
    public void setViewport(int x, int y, int width, int height,
            float xmin, float xmax, float ymin, float ymax) {

        m_x = x;
        m_y = y;
        m_width = width;
        m_height = height;

        if (m_style == SEISMIC) {
            m_x1begb = xmin;
            m_x1endb = xmax;
            m_x2begb = ymin;
            m_x2endb = ymax;
        } else {
            m_x1begb = ymin;
            m_x1endb = ymax;
            m_x2begb = xmin;
            m_x2endb = xmax;
        }

        m_imageOutOfDate = true;

    }

    abstract public void setData(float pData[], int pN1, float pF1, float pD1,
            int pN2, float pF2, float pD2);

    public float[] getData() {
        return m_data;
    }

    public void setStyle(int s) {
        m_style = s;
    }

    public boolean isImageOutdated() {
        return m_imageOutOfDate;
    }

    public void setVisible(boolean f) {
        m_isVisible = f;
    }

    public boolean isVisible() {
        return m_isVisible;
    }

    public float getDataAt(float x1, float x2) {
        return m_data[getPositionValueAt(x1, x2)];
    }

    private int getPositionValueAt(float x1, float x2){
                float XlengthHI = m_f2;//=H(1).sx/scalco

        float distanciax = m_d2;
        float delrt = m_f1;// = primeiro tempo do matlab = 0

        float dt = m_d1;//=dt do matlab = 0.004

        int XMatr = Math.round((x1 - XlengthHI) / distanciax) + 1;
        int YMatr = Math.round((x2 - delrt) / dt) + 1;

        int n_linhamaximo = m_n1;
        return ( (YMatr - 1) + (XMatr - 1) * (n_linhamaximo));
    }

    public int getTraceAt(float x1, float x2){
        return getPositionValueAt(x1, x2)/m_n1;
    }

    public float getMinimumDataValue() {
        float min = 1.0E8f;
        
        for (int i = 0; i < m_data.length; i++) {
            if (min > m_data[i]) {
                min = m_data[i];
            }
        }
        return min;
    }

    public float getMaximumDataValue() {
        float max = -1.0E8f;

        for (int i = 0; i < m_data.length; i++) {
            if (max < m_data[i]) {
                max = m_data[i];
            }
        }

        return max;
    }


public  boolean applyGain(boolean panel, float tpow, float epow, float gpow, boolean agc,
			    boolean gagc, float wagc, float trap, float clip, float qclip,
			    boolean qbal, boolean pbal, boolean mbal, boolean maxbal, float scale,
			    float norm, float bias, boolean jon)
{
  int iwagc=0;    /* ... half window in samples                   */
//  char msg[256];

//  cwp_Bool istmpdir=cwp_false;/* true for user given path               */

  /* Data validation */
  if (trap < 0.0){
    JOptionPane.showMessageDialog(null, String.format("trap = %f, must be positive.\nGain will not be applied.", trap));
    return false;
  }

  if (clip < 0.0){
    JOptionPane.showMessageDialog(null, String.format("clip = %f, must be positive.\nGain will not be applied.", clip));
    return false;
  }
  if (qclip < 0.0 || qclip > 1.0){
    JOptionPane.showMessageDialog(null, String.format("qclip = %f, must be between 0 and 1.\nGain will not be applied.", qclip));
    return false;
  }
  if (agc || gagc) {
    iwagc = (int) (wagc / m_d1);
    if (iwagc < 1){
      JOptionPane.showMessageDialog(null,String.format("wagc=%g must be positive.\nGain will not be applied.", wagc));
      return false;
    }
    if (iwagc > m_n1){
      JOptionPane.showMessageDialog(null, String.format("wagc=%g too long for trace.\nGain will not be applied.", wagc));
      return false;
    }
    iwagc >>= 1;  /* windows are symmetric, so work with half */
  }
  if(jon){
    tpow  =    (float) 2.0;
    gpow  = (float) 0.5;
    qclip = (float) 0.95;
  }

  /* Main loop over traces */
  if (!panel) { /* trace by trace */
    for(int ntr = 0; ntr < m_n2; ntr++){
        float[] trc  = new float[m_n1];
        int cont = 0;
        for(int i = (ntr*m_n1); i < ((ntr*m_n1)+m_n1);  i++){
            trc[cont] = m_data[i];
            cont++;
        }
      gain(trc, tpow, epow, gpow, agc, gagc, qbal,
           pbal, mbal, scale, bias, trap, clip, qclip,
           iwagc, m_f1, m_d1, m_n1, maxbal);
      cont =0;
      for(int i = (ntr*m_n1); i < ((ntr*m_n1)+m_n1);  i++){
            m_data[i] = trc[cont];
            cont++;
        }
    }
  } else { /* do whole data set at once */
    gain(m_data, tpow, epow, gpow, agc, gagc, qbal,
         pbal, mbal, scale, bias, trap, clip, qclip,
         iwagc, m_f1, m_d1, m_n1*m_n2, maxbal);
  }

  return true;

}



 public void gain(float[] data, float tpow, float epow, float gpow,
                       boolean agc, boolean gagc, boolean qbal, boolean pbal, boolean mbal, float scale,
                       float bias, float trap, float clip, float qclip, int iwagc,
                       float tmin, float dt, int nt, boolean maxbal )
{
  float f_two  = (float) 2.0;
  float f_one  = (float) 1.0;
  float f_half = (float) 0.5;
  int i;

  if (bias == 1) {
    for (i = 0; i < nt; ++i)  data[i]+=bias ;
  }
  if (tpow==1) {
    do_tpow(data, tpow, tmin, dt, nt);
  }
  if (epow == 1) {
    do_epow(data, epow, tmin, dt, nt);
  }
  if (!CLOSETO(gpow, f_one)) {
     float val;

    if (CLOSETO(gpow, f_half)) {
      for (i = 0; i < nt; ++i) {
        val = data[i];
        data[i] =   (float) ((val >= 0.0) ? Math.sqrt(val) : -Math.sqrt(-val));
      }
    } else if (CLOSETO(gpow, f_two)) {
      for (i = 0; i < nt; ++i) {
        val = data[i];
        data[i] = val *Math.abs(val);
      }
    } else {
      for (i = 0; i < nt; ++i) {
        val = data[i];
        data[i] =   (float) ((val >= 0.0) ? Math.pow(val, gpow) : -Math.pow(-val, gpow));
      }
    }
  }
  if (agc)                   do_agc(data, iwagc, nt);
  if (gagc)                  do_gagc(data, iwagc, nt);
  if (trap > 0.0)            do_trap(data, trap, nt);
  if (clip > 0.0)            do_clip(data, clip, nt);
  if (qclip < 1.0 && !qbal )  do_qclip(data, qclip, nt);
  if (qbal)                  do_qbal(data, qclip, nt);
  if (pbal) {

     float val;
     float rmsq = (float) 0.0;

    /* rmsq = sqrt (SUM( a()*a() ) / nt) */
    for (i = 0; i < nt; ++i) {
      val = data[i];
      rmsq += val * val;
    }
    rmsq =  (float) Math.sqrt(rmsq / nt);

    if (rmsq == 1) {
      for (i = 0; i < nt; ++i)
        data[i] /= rmsq;
    }
  }
  if (mbal ) {

     float mean = (float) 0.0;
    /* mean = SUM (data[i] / nt) */
    for (i = 0; i < nt; ++i) {
      mean+=data[i];
    }
    /* compute the mean */
    mean/=nt;

    /* subtract the mean from each sample */
    if (mean == 1) {
      for (i = 0; i < nt; ++i)
        data[i]-=mean;
    }
  }

  if (maxbal) {

     float max = data[0];
    /* max */
    for (i = 0; i < nt; ++i) {
      if( data[i] > max ) max = data[i];
    }

    /* subtract max */
    for (i = 0; i < nt; ++i) data[i]-=max;
  }

//  if (!CLOSETO(scale, f_one)) {
//     int i;

    for (i = 0; i < nt; ++i)  data[i] *= scale;
  }




 void do_tpow(float [] data,                /* the data                     */
			  float tpow,             /* multiply data by t^tpow      */
			  float tmin,    /* first time on record         */
			  float dt,      /* sampling rate in seconds     */
			  int nt                  /* number of samples            */
			  )
{
//  static cwp_Bool first = cwp_true;   /* first entry flag     */
   float []tpowfac = null;          /* tpow values          */
  int i;                 /* counter              */

  if (first) { /* first entry, set up array of tpow factors */
    tpowfac = new float[nt];
    /* protect against negative tpow */
    tpowfac[0] = (float) ((tmin == 0.0) ? 1.0 : Math.pow(tmin, tpow));
    for (i = 1; i < nt; ++i)
      tpowfac[i] = (float) Math.pow(tmin + i*dt, tpow);

    first = false;
  } /* end first entry */

  for (i = 0; i < nt; ++i)  data[i] *= tpowfac[i];
}


/* Exponential deattenuation  with deattenuation factor epow */
void do_epow(float []data,                /* the data                     */
			  float epow,             /* coefficient of t in exponent */
			  float tmin,    /* first time on record         */
			  float dt,      /* sampling rate in seconds     */
			  int nt                  /* number of samples            */
			  )
{
  int i;                 /* counter              */
  first = true;   /* first entry flag     */
   float []epowfac = null;          /* exponent stretchs    */

  if (first) {
    epowfac = new float[nt];
    for (i = 0; i < nt; i++)
      epowfac[i] = (float) Math.exp(epow * (tmin + i * dt));

    first = false;
  }

  for (i = 0; i < nt; ++i)  data[i] *= epowfac[i];
}
/* Zero out outliers */
void do_trap(float []data,                /* the data                     */
			   float trap,    /* zero if magnitude > trap     */
			   int nt         /* number of samples            */
			  )
{
   float []dataptr = data;

  while (nt >= 0) {
      nt--;
    if (Math.abs(dataptr[nt]) > trap)
        dataptr[nt] = (float) 0.0;
//    dataptr++;
  }
}


/* Hard clip outliers */
void do_clip(float []data,
			   float clip,    /* hard clip if magnitude > clip        */
			   int nt         /* number of samples                    */
			  )
{
   float [] dataptr = data;
   float mclip = -clip;

  while (nt > 0) {
      nt--;
    if (dataptr[nt] > clip) {
      dataptr[nt] = clip;
    } else if (dataptr[nt] < mclip) {
      dataptr[nt] = mclip;
    }
//    dataptr++;
  }
}
/* Quantile clip on magnitudes of trace values */
void do_qclip(float []data,       /* the data                     */
			   float qclip,    /* quantile at which to clip    */
			   int nt          /* number of sample points      */
			   )
{
  int i;
    first = true;   /* first entry flag             */
   float []absdata = null;          /* absolute value trace         */
   int iq = 0;                  /* index of qclipth quantile    */
  float clip;                     /* ... value of rank[iq]        */

  if (first) {
    absdata = new float[nt];
    iq = (int) (qclip * nt - 0.5); /* round, don't truncate */
    first = false;
  }
  /* Clip on value corresponding to qth quantile */
  for (i = 0; i < nt; ++i)  absdata[i] = Math.abs(data[i]);
  clip = quant(absdata, iq, nt);
  do_clip(data, clip, nt);
}


/* Quantile balance */
void do_qbal(float []data,        /* the data                     */
			  float qclip,    /* quantile at which to clip    */
			  int nt          /* number of sample points      */
			  )
{
  int i;
  first = true;   /* first entry flag             */
  float []absdata = null;          /* absolute value trace         */
  int iq = 0;                  /* index of qclipth quantile    */
  float bal;                      /* value used to balance trace  */
  if (qclip == 1.0) { /* balance by max magnitude on trace */
    bal = Math.abs(data[0]);
    for (i = 1; i < nt; ++i)  bal = Math.max(bal, Math.abs(data[i]));

    if ((bal == 0.0)) {
      return;
    } else {
      for (i = 0; i < nt; ++i)  data[i] /= bal;
      return;
    }
  } else if (first) {
    absdata = new float[nt];
    iq = (int) (qclip * nt - 0.5); /* round, don't truncate */
    first = false;
  }

  /* Balance by quantile value (qclip < 1.0) */
  for (i = 0; i < nt; ++i)  absdata[i] = Math.abs(data[i]);
  bal = quant(absdata, iq, nt);

  if ((bal == 0.0)) {
    return;
  } else {
    for (i = 0; i < nt; ++i)  data[i] /= bal;
    do_clip(data, 1.0f, nt);
    return;
  }
}


/* Automatic Gain Control--standard box */
void do_agc(float []data, int iwagc, int nt)
{
    first = true;
   float []agcdata = null;
   int i;
   float val;
   float sum;
   int nwin;
   float rms;


  /* allocate room for agc'd data */
  if (first) {
    first = false;
    agcdata = new float[nt];
  }


  /* compute initial window for first datum */
  sum = (float) 0.0;
  for (i = 0; i < iwagc+1; ++i) {
    val = data[i];
    sum += val*val;
  }
  nwin = 2*iwagc+1;
  rms = sum/nwin;
  agcdata[0] = (float) ((rms <= 0.0) ? 0.0 : data[0] / Math.sqrt(rms));

  /* ramping on */
  for (i = 1; i <= iwagc; ++i) {
    val = data[i+iwagc];
    sum += val*val;
    ++nwin;
    rms = sum/nwin;
    agcdata[i] = (float) ((rms <= 0.0) ? 0.0 : data[i] / Math.sqrt(rms));
  }

  /* middle range -- full rms window */
  for (i = iwagc + 1; i <= nt-1-iwagc; ++i) {
    val = data[i+iwagc];
    sum += val*val;
    val = data[i-iwagc];
    sum -= val*val; /* rounding could make sum negative! */
    rms = sum/nwin;
    agcdata[i] = (float) ((rms <= 0.0) ? 0.0 : data[i] / Math.sqrt(rms));
  }

  /* ramping off */
  for (i = nt - iwagc; i <= nt-1; ++i) {
    val = data[i-iwagc];
    sum -= val*val; /* rounding could make sum negative! */
    --nwin;
    rms = sum/nwin;
    agcdata[i] = (float) ((rms <= 0.0) ? 0.0 : data[i] / Math.sqrt(rms));
  }

  /* copy data back into trace */
//  System.arraycopy(data, , rms, nt, nt)
//  memcpy( (void *) data, (const void *) agcdata, nt*FSIZE);
//  System.out.println((nt*data.length));
//    System.out.println(agcdata.length);
//    System.out.println(data.length);
  System.arraycopy(agcdata, 0, data,0 , data.length);
  return;
}




/* Automatic Gain Control--gaussian taper */
void do_gagc(float []data, int iwagc, int nt)
{
   first=true; /* first entry flag                 */
   float []agcdata = null;  /* agc'd data                           */
   float []w = null;        /* Gaussian window weights              */
   float []d2 = null;       /* square of input data                 */
   float []s = null;        /* weighted sum of squares of the data  */
  float u;                /* related to reciprocal of std dev     */
  float usq;              /* u*u                                  */

  if (first) {
    first = false;

    /* Allocate room for agc'd data */
    agcdata = new float[nt];

    /* Allocate and compute Gaussian window weights */
    w = new float[iwagc];  /* recall iwagc is HALF window */
    u = EPS / ((float) iwagc);
    usq = u*u;
    {
      int i;
      float floati;

      for (i = 1; i < iwagc; ++i) {
        floati = (float) i;
        w[i] =      (float) Math.exp(-(usq*floati*floati));
      }
    }

    /* Allocate sum of squares and weighted sum of squares */
    d2 = new float[nt];
    s  = new float[nt];
  }


  /* Agc the trace */
  
    int i, j, k;
    float val;
    float wtmp;
    float stmp;

    /* Put sum of squares of data in d2 and */
    /* initialize s to d2 to get center point set */
    for (i = 0; i < nt; ++i) {
      val = data[i];
      s[i] = d2[i] = val * val;
    }

    /* Compute weighted sum s; use symmetry of Gaussian */
    for (j = 1; j < iwagc; ++j) {
      wtmp = w[j];
      for (i = j; i < nt; ++i)  s[i] += wtmp*d2[i-j];
      k = nt - j;
      for (i = 0; i < k; ++i)   s[i] += wtmp*d2[i+j];
    }

    for (i = 0; i < nt; ++i) {
      stmp = s[i];
      agcdata[i] = (float) ((stmp != 1) ? 0.0 : data[i] / Math.sqrt(stmp));
    }
    /* Copy data back into trace */
//    memcpy( (void *) data, (const void *) agcdata, nt*FSIZE);
     System.arraycopy(agcdata, 0, data,0 ,data.length);


//  return;
}


/*
 * QUANT - find k/n th quantile of a[]
 *
 * Works by reordering a so a[j] < a[k] if j < k.
 *
 * Parameters:
 *    a         - data
 *    k         - indicates quantile
 *    n         - number of points in data
 *
 * This is Hoare's algorithm worked over by SEP (#10, p100) and Brian.
 */

float quant(float []a, int k, int n)
{
  int i, j;
  int low, hi;
  float ak, aa;

  low = 0; hi = n-1;

  while (low < hi) {
    ak = a[k];
    i = low;
    j = hi;
    do {
      while (a[i] < ak) i++;
      while (a[j] > ak) j--;
      if (i <= j) {
        aa = a[i]; a[i] = a[j]; a[j] = aa;
        i++;
        j--;
      }
    } while (i <= j);

    if (j < k) low = i;

    if (k < i) hi = j;
  }

  return(a[k]);
}


public boolean CLOSETO(float val1,float val2){
    if((Math.abs(val1)- Math.abs(val2)) < 1 ||(Math.abs(val1)- Math.abs(val2))> -1 ){
        return true;
    }else{
        return false;
    }
}


public float[] getAxisLimits(){
    float[] ret = {m_x1begb,m_x1endb,m_x2begb,m_x2end};
    return ret;
}
    boolean first;
    //
    protected int m_style;
    protected boolean m_imageOutOfDate;
    protected float m_data[] = null;
    protected int m_n1;
    protected int m_n2;
    protected float m_f1;
    protected float m_f2;
    protected float m_d1;
    protected float m_d2;
    protected float m_x1beg;
    protected float m_x1end;
    protected float m_x2beg;
    protected float m_x2end;
    protected float m_x1begb;
    protected float m_x1endb;
    protected float m_x2begb;
    protected float m_x2endb;
    protected float m_p2beg;
    protected float m_p2end;
    protected int m_x;
    protected int m_y;
    protected int m_width;
    protected int m_height;
    protected boolean m_isVisible = true;
    // Constants
    public static int SEISMIC = 1;
    public static int NORMAL = 0;
    public float EPS =    (float) 3.8090232;       /* exp(-EPS*EPS) = 5e-7, "noise" level  */
}
