
package client;

import static client.BattagliaNavale.map;
import java.io.Serializable;

public class Nave implements Serializable
{
    // false - verticale
    // true - orizz
    public boolean orientamento;
    public int length = 0;
    int r = 0;
    int c = 0;
    
    public Nave(int l) 
    {
        orientamento = false;
        length = l;
    }
    
    public Nave(boolean orient, int l, int r, int c) 
    {
        orientamento = orient;
        length = l;
        this.r = r;
        this.c = c;
    }
    
    public boolean positionShip()
    {
        boolean ok = true;
        try
        {
            int k=0;
            if(orientamento)
                k=c;
            else
                k=r;


                 if(orientamento)
                 {
                    for(int i = c;i<c+length;i++)
                    {
                        //map[r][i].setState(1);
                        map[r][i].setTextByState();
                        System.out.print(r+" "+i);
                    }
                 }
                 else
                 {
                     for(int i = r;i<r+length;i++)
                    {
                        //map[i][c].setState(1);
                        map[i][c].setTextByState();
                    }
                 }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException ex){ok=false;}
        
        if(ok)
        {
            if(orientamento)
                 {
                    for(int i = c;i<c+length;i++)
                    {
                        map[r][i].setState(1);
                        map[r][i].setIdNave(length);
                        map[r][i].setTextByState();
                        
                        System.out.print(map[r][i].getIdNave()+" "+length);
                    }
                 }
                 else
                 {
                     for(int i = r;i<r+length;i++)
                    {
                        map[i][c].setState(1);
                        map[i][c].setIdNave(length);
                        map[i][c].setTextByState();
                       
                        System.out.print(map[i][c].getIdNave()+" "+length);
                    } 
                 }
        }
        return ok;
    }
   
    
}
