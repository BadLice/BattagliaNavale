
package client;

import javafx.scene.control.Button;

public class Ship extends Button
{
    // 0 - vuoto(mare)
    // 1 - nave
    // 2 - colpito
    // 3 - colpito vuoto
    private int state = 0;
    int width = 40;
    int height = 40;
    int r = 0;
    int c = 0;
    int idNave;
    String styleStr="-fx-opacity: 1.0;-fx-border-width:0.5;-fx-border-color:black;-fx-background-position: center center; -fx-background-repeat: stretch; -fx-background-size: 40 40;-fx-background-color:#66ccff;";

    public int getIdNave() {
        return idNave;
    }

    public void setIdNave(int idNave) {
        this.idNave = idNave;
    }
    
    public Ship(int state,int r,int c)
    {
        this.state = state;
        setTextByState();
        this.setPrefSize(width,height);
        this.r=r;
        this.c=c;
        //this.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        this.setStyle(styleStr);
        this.setOnMouseEntered(e ->{this.setStyle(this.getStyle()+"-fx-background-color:#a5e0ff;");});
        this.setOnMouseExited(e ->{this.setTextByState();});
        
        
    }
       
    public void setTextByState()
    {
        switch(state)
        {
            case 0:
               this.setStyle(styleStr);
                break;
            case 1:
                //this.setText("N");
                this.setStyle(styleStr + "-fx-background-image: url('img/ship.png');");
                break;
            case 2:
                //this.setText("C");
                this.setStyle(styleStr + "-fx-background-image: url('img/explosion.png');");
                break;
            case 3:
                //this.setText("X");
                this.setStyle(styleStr + "-fx-background-image: url('img/shot.png');");
                break;
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) 
    {
        this.state = state;
        setTextByState();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    
}
