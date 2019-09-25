package com.dialectek.orac;

import java.util.*;

import weka.core.*;
import weka.classifiers.*;

/**
 * K-nearest neighbor algorithm. Parameters are the number of nearest neighbors k,
 * 'distance function and the distance weighting. For more
 * 'information, see:\n Batista, G. E. A. P. A.,
 * 'Data pre-processing for supervised machine learning', available at:
 * http://www.teses.usp.br/teses/disponiveis/55/55134/tde-06102003-160219/
 *
 * Parameters:
 *
 * -K <number of neighbors>
 * Number of nearest neighbors.
 * (default K=1)
 *
 * -V
 * Heterogeneous Distance Value Metric (HVDM).
 *
 * -M
 * Heterogeneous Manhattan-Overlap Metric (HMOM).
 *
 * -I
 * Weight neighbors by inverse of their squared distance.
 *
 * -S
 * Weight neighbors by similarity.
 *
 * -U
 * Forces each instance classified to be included in the training set.
 *
 * @author Diego Furtado Silva (diegofurts@grad.icmc.usp.br)
 */
public class Knn extends Classifier implements OptionHandler {
   private static final long serialVersionUID = 2938798765409321198L;


   /** Class constructors. **/
   public Knn(int k)
   {
      setKnn(k);
      m_Distance  = DIST_HEOM;
      m_Weighting = WEIGHT_NONE;
      m_Upd       = UPD_FALSE;
   }


   public Knn()
   {
      setKnn(5);
      m_Distance  = DIST_HEOM;
      m_Weighting = WEIGHT_INV;
      m_Upd       = UPD_FALSE;
   }


   /** Description of the classifier in Weka's graphical mode. **/
   public String globalInfo()
   {
      return("K-nearest neighbor algorithm. Parameters are the number k, " +
             "the distance function and the distance weighting. For more " +
             "information, see:\n Batista, G. E. A. P. A., " +
             "\"Data pre-processing for supervised machine learning\", available at:\n" +
             "http://www.teses.usp.br/teses/disponiveis/55/55134/tde-06102003-160219/");
   }


   /** Instances used in training. **/
   private Instances m_Train;


   /**
    * Stores the maximum and minimum values and standard deviation
    * of each attribute (depending on the metric used).
    **/
   private double[] m_Min, m_Max, m_StdDev;

   /** Number of nearest neighbor (k). **/
   private int m_Knn;
   public void setKnn(int m_Knn) { this.m_Knn = m_Knn; }
   public int getKnn() { return(this.m_Knn); }


   /** Distance function to be used in the algorithm. **/
   private int m_Distance;

   public void setDistance(SelectedTag newMethod)
   {
      if (newMethod.getTags() == TAGS_DISTANCE)
      {
         this.m_Distance = newMethod.getSelectedTag().getID();
      }
   }


   public SelectedTag getDistance()
   {
      return(new SelectedTag(this.m_Distance, TAGS_DISTANCE));
   }


   public static final int DIST_HEOM      = 1;
   public static final int DIST_HVDM      = 2;
   public static final int DIST_MANHATTAN = 3;
   public static final     Tag [] TAGS_DISTANCE =
   {
      new Tag(DIST_HEOM,      "Heterogeneous Euclidean-Overlap Metric"),
      new Tag(DIST_MANHATTAN, "Heterogeneous Manhattan-Overlap Metric"),
      new Tag(DIST_HVDM,      "Heterogeneous Value Distance Function")
   };

   /** Structures to store data to calculate the VDM metric. **/
   Vector<double[]>   m_VetVDM = new Vector();
   Vector<double[][]> m_MatVDM = new Vector();


   /** Distance weighting. **/
   private int m_Weighting;

   public void setWeighting(SelectedTag newMethod)
   {
      if (newMethod.getTags() == TAGS_WEIGHTING)
      {
         this.m_Weighting = newMethod.getSelectedTag().getID();
      }
   }


   public SelectedTag getWeighting()
   {
      return(new SelectedTag(this.m_Weighting, TAGS_WEIGHTING));
   }


   public static final int WEIGHT_NONE = 1;
   public static final int WEIGHT_INV  = 2;
   public static final int WEIGHT_SIM  = 3;
   public static final     Tag [] TAGS_WEIGHTING =
   {
      new Tag(WEIGHT_NONE, "No weight"),
      new Tag(WEIGHT_INV,  "1/(distance^2)"),
      new Tag(WEIGHT_SIM,  "1-distance")
   };

   /** This parameter forces each classified instance
    * to be included in the training set.
    **/
   private int m_Upd;

   public void setUpd(SelectedTag newMethod)
   {
      if (newMethod.getTags() == TAGS_UPD)
      {
         this.m_Upd = newMethod.getSelectedTag().getID();
      }
   }


   public SelectedTag getUpd()
   {
      return(new SelectedTag(this.m_Upd, TAGS_UPD));
   }


   public static final int UPD_FALSE = 0;
   public static final int UPD_TRUE  = 1;
   public static final     Tag [] TAGS_UPD =
   {
      new Tag(UPD_FALSE, "False"),
      new Tag(UPD_TRUE,  "True")
   };

   /** Description of parameters **/
   public String knnTipText()
   {
      return("Number of nearest neighbors (k).");
   }


   public String distanceTipText()
   {
      return("Distance function.");
   }


   public String weightingTipText()
   {
      return("Distance weighting.");
   }


   public String updTipText()
   {
      return("This parameter forces each classified instance" +
             "to be included in the training set.");
   }


   /** Parses a given list of options. **/
   public void setOptions(String[] options) throws Exception
   {
      String knnString = Utils.getOption('K', options);

      if (knnString.length() != 0)
      {
         setKnn(Integer.parseInt(knnString));
      }
      else
      {
         setKnn(1);
      }

      if (Utils.getFlag('V', options))
      {
         setDistance(new SelectedTag(DIST_HVDM, TAGS_DISTANCE));
      }
      else if (Utils.getFlag('M', options))
      {
         setDistance(new SelectedTag(DIST_MANHATTAN, TAGS_DISTANCE));
      }
      else
      {
         setDistance(new SelectedTag(DIST_HEOM, TAGS_DISTANCE));
      }

      if (Utils.getFlag('I', options))
      {
         setWeighting(new SelectedTag(WEIGHT_INV, TAGS_WEIGHTING));
      }
      else if (Utils.getFlag('S', options))
      {
         setWeighting(new SelectedTag(WEIGHT_SIM, TAGS_WEIGHTING));
      }
      else
      {
         setWeighting(new SelectedTag(WEIGHT_NONE, TAGS_WEIGHTING));
      }

      if (Utils.getFlag('U', options))
      {
         setUpd(new SelectedTag(UPD_TRUE, TAGS_UPD));
      }
      else
      {
         setUpd(new SelectedTag(UPD_FALSE, TAGS_UPD));
      }

      Utils.checkForRemainingOptions(options);
   }


   /** Gets the current settings of Knn */
   public String [] getOptions()
   {
      String [] options = new String [11];
      int current = 0;
      options[current++] = "-K";
      options[current++] = "" + getKnn();

      if (m_Distance == DIST_HVDM)
      {
         options[current++] = "-V";
      }

      if (m_Distance == DIST_MANHATTAN)
      {
         options[current++] = "-M";
      }

      if (m_Weighting == WEIGHT_INV)
      {
         options[current++] = "-I";
      }

      if (m_Weighting == WEIGHT_SIM)
      {
         options[current++] = "-S";
      }

      if (m_Upd == UPD_TRUE)
      {
         options[current++] = "-U";
      }

      while (current < options.length)
      {
         options[current++] = "";
      }

      return(options);
   }


   /** Returns an enumeration describing the available options. **/
   public Enumeration listOptions()
   {
      Vector newVector = new Vector(4);

      newVector.addElement(new Option(
                              "\tNumber of nearest neighbors (k).\n"
                              + "\t(Default = 1)",
                              "K", 1, "-K <number of neighbors>"));

      newVector.addElement(new Option(
                              "\tHeterogeneous Euclidean-Value Distance Metric.\n",
                              "V", 0, "-V"));

      newVector.addElement(new Option(
                              "\tHeterogeneous Manhattan-Overlap Metric.\n",
                              "M", 0, "-M"));

      newVector.addElement(new Option(
                              "\tWeight neighbors by inverse of their squared distance.\n",
                              "I", 0, "-I"));

      newVector.addElement(new Option(
                              "\tWeight neighbors by similarity.\n",
                              "S", 0, "-S"));

      newVector.addElement(new Option(
                              "\tForces each classified instance to be included in the training set.\n",
                              "U", 0, "-U"));

      return(newVector.elements());
   }


   /** Vector used to store the neighbors according their proximity **/
   private Vector<Neighbor> neighbors = new Vector<Neighbor>();

   /** Class that defines a neighbor **/
   protected class Neighbor {
      double   dist;
      Instance inst;

      Neighbor(double dist, Instance inst)
      {
         this.dist = dist;
         this.inst = inst;
      }


      public double getDist() { return(dist); }
      public Instance getInst() { return(inst); }
   }

   double m_Bigger;


   /** Generates the classifier. **/
   public void buildClassifier(Instances data)
   {
      m_Train = new Instances(data, 0, data.numInstances());

      m_Train.deleteWithMissingClass();

      //Initializes the vectors and determines the initial values to calculate the HVDM function
      if (m_Distance == DIST_HVDM)
      {
         m_StdDev = new double [m_Train.numAttributes()];

         for (int i = 0; i < m_Train.numAttributes(); i++)
         {
            if (m_Train.attribute(i).isNominal())
            {
               m_VetVDM.add(new double[m_Train.attribute(i).numValues()]);
               m_MatVDM.add(new double[m_Train.attribute(i).numValues()][m_Train.numClasses()]);
            }
            else
            {
               //Calculates the standard deviation for each attribute
               AttributeStats as = m_Train.attributeStats(i);
               m_StdDev[i] = as.numericStats.stdDev;

               m_VetVDM.add(new double[0]);
               m_MatVDM.add(new double[0][0]);
            }
         }
      }
      else
      {
         m_Min = new double [m_Train.numAttributes()];
         m_Max = new double [m_Train.numAttributes()];
      }

      evaluateData();
   }


   /** Includes the classified instance into training set. **/
   public void updClassifier(Instance instance) throws Exception
   {
      if (instance.classIsMissing())
      {
         return;
      }

      updData(instance);

      m_Train.add(instance);
   }


   /** Classify an instance. **/
   public double classifyInstance(Instance instance) throws Exception
   {
      double dist, mm;
      int    i;

      neighbors.clear();
      m_Bigger = Double.NaN;

      Enumeration enu = m_Train.enumerateInstances();
      while (enu.hasMoreElements())
      {
         //Current instance
         Instance trainInstance = (Instance)enu.nextElement();

         dist = distance(instance, trainInstance);

         //If the calculated distance is greater than Kth neighbor, returns -1
         if (dist > 0)
         {
            Neighbor v = new Neighbor(dist, trainInstance);

            if (neighbors.size() == 0)
            {
               neighbors.add(v);
            }
            else
            {
               if ((neighbors.size() >= m_Knn))
               {
                  i = search(0, m_Knn - 1, dist);
               }
               else
               {
                  i = search(0, neighbors.size() - 1, dist);
               }

               neighbors.add(i, v);
            }

            if ((neighbors.size() >= m_Knn))
            {
               m_Bigger = neighbors.elementAt(m_Knn - 1).getDist();
            }
         }
      }

      mm = meanOrMode();

      if (m_Upd == UPD_TRUE)
      {
         instance.setClassValue(mm);
         updClassifier(instance);
      }

      return(mm);
   }


   /** Function that seeks the position where a neighbor should be inserted into the
    * vector, based on binary search. **/
   public int search(int begin, int end, double value)
   {
      int center = (int)(begin + end) / 2;

      while (begin <= end)
      {
         center = (int)(begin + end) / 2;

         if (neighbors.elementAt(center).getDist() < value)
         {
            if ((center < neighbors.size() - 1) &&
                (neighbors.elementAt(center + 1).getDist() >= value))
            {
               return(center + 1);
            }

            begin = center + 1;
         }
         else if (neighbors.elementAt(center).getDist() > value)
         {
            if ((center > 0) &&
                (neighbors.elementAt(center - 1).getDist() <= value))
            {
               return(center);
            }

            end = center - 1;
         }
         else
         {
            return(center);
         }
      }

      if (neighbors.elementAt(center).getDist() < value)
      {
         return(center + 1);
      }
      else
      {
         return(center);
      }
   }


   /** Searches the training data to find the maximum and minimum values
    * or the values used in VDM function. **/
   public void evaluateData()
   {
      //Enumerate instances
      Enumeration enu = m_Train.enumerateInstances();

      while (enu.hasMoreElements())
      {
         //Current instances
         Instance trainInstance = (Instance)enu.nextElement();

         //Searches attributes of the current instance
         for (int i = 0; i < m_Train.numAttributes(); i++)
         {
            //If the attribute is numeric, evaluates the min and max
            if ((m_Distance != DIST_HVDM) &&
                (m_Train.attribute(i).isNumeric()) &&
                (!trainInstance.isMissing(i)))
            {
               if ((m_Min[i] == m_Max[i]) && (trainInstance.value(i) > m_Max[i]))
               {
                  m_Min[i] = trainInstance.value(i);
                  m_Max[i] = trainInstance.value(i);
               }
               else
               {
                  if (trainInstance.value(i) < m_Min[i])
                  {
                     m_Min[i] = trainInstance.value(i);
                  }
                  if (trainInstance.value(i) > m_Max[i])
                  {
                     m_Max[i] = trainInstance.value(i);
                  }
               }
            }
            //If the attribute is nominal and the distance function is HVDM, counts the number of occurences
            else if ((m_Distance == DIST_HVDM) &&
                     (m_Train.attribute(i).isNominal()) &&
                     (!trainInstance.isMissing(i)))
            {
               (m_VetVDM.elementAt(i))[(int)trainInstance.value(i)]++;
               (m_MatVDM.elementAt(i))[(int)trainInstance.value(i)][(int)trainInstance.classValue()]++;
            }
         }
      }
   }


   /** If data is updated, then this function reviews the data to determine new maximum and minimum values
    * or new values used in VDM function. */
   public void updData(Instance inst)
   {
      //Searches attributes of the instance
      for (int i = 0; i < m_Train.numAttributes(); i++)
      {
         //If the attribute is numeric, evaluates the min and max
         if ((m_Train.attribute(i).isNumeric()) && (!inst.isMissing(i)))
         {
            if (m_Distance == DIST_HVDM)
            {
               AttributeStats as = m_Train.attributeStats(i);
               m_StdDev[i] = as.numericStats.stdDev;
            }

            if (Double.isNaN(m_Min[i]))
            {
               m_Min[i] = inst.value(i);
               m_Max[i] = inst.value(i);
            }
            else
            {
               if (inst.value(i) < m_Min[i])
               {
                  m_Min[i] = inst.value(i);
               }

               if (inst.value(i) > m_Max[i])
               {
                  m_Max[i] = inst.value(i);
               }
            }
         }
         //If the attribute is nominal and the distance function is HVDM, counts the number of occurences
         else if ((m_Distance == DIST_HVDM) && (m_Train.attribute(i).isNominal()) &&
                  (!inst.isMissing(i)))
         {
            (m_VetVDM.elementAt(i))[(int)inst.value(i)]++;
            (m_MatVDM.elementAt(i))[(int)inst.value(i)][(int)inst.classValue()]++;
         }
      }
   }


   /** Caculates the distance between two instances. **/
   public double distance(Instance inst1, Instance inst2)
   {
      double dist = 0;

      //Searches attributes of the instance
      for (int i = 0; i < m_Train.numAttributes(); i++)
      {
         //Do not calculate distance of class attribute
         if (i == m_Train.classIndex())
         {
            continue;
         }

         //If one or both value are missing, sum the maximum distance
         if (inst1.isMissing(i) || inst2.isMissing(i))
         {
            dist += 1;
         }
         else
         {
            //Nominal attribute
            if (m_Train.attribute(i).isNominal())
            {
               if (m_Distance == DIST_HVDM)
               {
                  if ((int)inst1.value(i) != (int)inst2.value(i))
                  {
                     dist += norm_vdm(inst1, inst2, i);
                  }
               }
               else
               {
                  if ((int)inst1.value(i) != (int)inst2.value(i))
                  {
                     dist += 1;
                  }
               }

               //Numeric attribute
            }
            else
            {
               if (m_Distance == DIST_HVDM)
               {
                  dist += norm_diff(inst1, inst2, i);
               }
               else
               {
                  if (!(Double.isNaN(m_Min[i])) && !(Utils.eq(m_Max[i], m_Min[i])))
                  {
                     dist += range_norm_diff(inst1, inst2, i);
                  }
                  else
                  {
                     dist += 1;
                  }
               }
            }
         }

         if (!Double.isNaN(m_Bigger) && (dist > m_Bigger))
         {
            return(-1);
         }
      }

      return(dist);
   }


   /** Calculates the normalized difference between two numeric values. **/
   public double norm_diff(Instance inst1, Instance inst2, int att)
   {
      if (m_StdDev[att] == 0) { return(1); }

      return(Math.pow((inst2.value(att) - inst1.value(att)) /
                      (4 * m_StdDev[att]), 2));
   }


   /** Calculates the range normalized difference between two numeric values. **/
   public double range_norm_diff(Instance inst1, Instance inst2, int att)
   {
      double d;

      if (m_Max[att] == m_Min[att]) { return(1); }

      if (m_Distance == DIST_MANHATTAN)
      {
         d = Math.abs((inst2.value(att) - inst1.value(att)) /
                      (m_Max[att] - m_Min[att]));
         return(d);
      }
      else
      {
         d = (inst2.value(att) - inst1.value(att)) /
             (m_Max[att] - m_Min[att]);
         return(d * d);
      }
   }


   /** Calculates the distance between two nominal values using the VDM function **/
   public double norm_vdm(Instance inst1, Instance inst2, int att)
   {
      int i;
      int ncl = m_Train.numDistinctValues(m_Train.classIndex());

      double d      = 0, nx1, nx2;
      double nx1C[] = new double[ncl];
      double nx2C[] = new double[ncl];

      nx1 = m_VetVDM.elementAt(att)[(int)inst1.value(att)];
      nx2 = m_VetVDM.elementAt(att)[(int)inst2.value(att)];

      nx1C = m_MatVDM.elementAt(att)[(int)inst1.value(att)];
      nx2C = m_MatVDM.elementAt(att)[(int)inst2.value(att)];

      for (i = 0; i < ncl; i++)
      {
         if ((nx1 > 0) && (nx2 > 0))
         {
            d += Math.pow(((nx1C[i] / nx1) - (nx2C[i] / nx2)), 2.0);
         }
         else
         {
            return(1.0);
         }
      }

      //(sqrt(d))^2==d
      return(d);
   }


   /** Calculates mean or mode of k-nearest neighbors
    *  considering the distance weighting. **/
   public double meanOrMode()
   {
      int i, aux, k = getKnn();

      int n = m_Train.numAttributes();

      double dist = 0, m = 0, peso = 0, soma = 0, maior = 0;

      boolean out  = false;
      boolean zero = false;

      if (m_Train.classAttribute().isNumeric())
      {
         //Calculates the mean
         for (i = 0; i < k && !out; i++)
         {
            Neighbor v = neighbors.elementAt(i);

            Instance inst = v.getInst();

            //In this case, if exists neighbors with distance 0, calculates
            //the mean only among them
            if (m_Weighting == WEIGHT_INV)
            {
               dist = v.getDist();

               if (!zero && (dist == 0))
               {
                  zero = true;
               }


               if (!zero)
               {
                  peso  = 1 / dist;
                  m    += (peso * inst.value(inst.classIndex()));
                  soma += peso;
               }
               else if (dist == 0)
               {
                  m += inst.value(inst.classIndex());
                  soma++;
               }
               else
               {
                  out = true;
               }
            }
            else if (m_Weighting == WEIGHT_SIM)
            {
               dist = v.getDist();

               peso = n - dist;

               m    += (peso * inst.value(inst.classIndex()));
               soma += peso;
            }
            else
            {
               m += inst.value(inst.classIndex());
               soma++;
            }
         }

         m /= soma;
      }
      //Calculates the mode
      else
      {
         int    t     = m_Train.numDistinctValues(m_Train.classIndex());
         double vet[] = new double[t];

         for (i = 0; !out; i++)
         {
            if (i >= neighbors.size())
            {
               out = true;
               continue;
            }

            Neighbor v = neighbors.elementAt(i);

            if ((i >= k) && (v.getDist() - dist > 0.0001))
            {
               out = true;
               continue;
            }

            dist = v.getDist();

            Instance inst = v.getInst();

            aux = (int)inst.value(inst.classIndex());

            //In this case, if exists neighbors with distance 0, calculates
            //the mode only among them
            if (m_Weighting == WEIGHT_INV)
            {
               if (!zero && (dist == 0))
               {
                  zero = true;
               }


               if (!zero)
               {
                  vet[aux] += (1 / (dist * dist));
               }
               else if (m_Weighting == WEIGHT_SIM)
               {
                  vet[aux]++;
               }
               else
               {
                  out = true;
               }
            }
            else if (m_Weighting == WEIGHT_SIM)
            {
               vet[aux] += (n - dist);
            }
            else
            {
               vet[aux]++;
            }

            if (vet[aux] > maior)
            {
               maior = vet[aux];
               m     = aux;
            }
         }
      }
      return(m);
   }


   /** Main method. **/
   public static void main(String[] argv)
   {
      try {
         System.out.println(Evaluation.evaluateModel(new Knn(), argv));
      }
      catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }
}
