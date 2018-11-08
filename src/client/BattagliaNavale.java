package client;

import static client.BattagliaNavale.in;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BattagliaNavale extends Application 
{
    //0 - scelta navi
    //1 - connessione al server
    //2 - disabilita map + mio turno
    //3 - read mossa
    //4 - mio turno
    //----------------------------------
    //3 - invio mossa
    //2 - invio risposta del controllo all'altro client
    static Scene scene;
    public static int state = 0;
    public static Stage primaryStage;
    public static Ship[][] map;
    public static Ship[][] enmap;
    public static Nave[] navi;
    public static TextField response;
    public static int IndexNave = 0;
    public static Label orientLabel;
    public static Socket client;
    public static DataInputStream in;
    public static DataOutputStream out;
    public static int turn;
    public static boolean b1=true, b2=false;
    public static int RIGA , COLONNA;
    public static String message="";
    public static Thread ReadThread;
    public static  Timer timer;
    public static Button orientBtn;
    
    @Override
    public void start(Stage primaryStage) 
    {
       this.primaryStage = primaryStage;
       stateHandler();
    }
    
    public void stateHandler()
    {
           switch(state)
            {
                case 0: 
                    choseShip();
                    break;
                case 1: connect();
                    break;
                case 2: state3();
                    break;
            }
    }
    
    private void choseShip()
    {
        AnchorPane root= new AnchorPane();
        root.setStyle("-fx-background-color:grey;");
        GridPane grid = new GridPane();
        //grid.setStyle("-fx-background-color:black;");
        grid.setLayoutX(70); grid.setLayoutY(25);
        
        /*orientLabel = new Label("orizzontale");
        orientLabel.set*/
        
        orientBtn = new Button();
        GridPane.setConstraints(orientBtn, 10, 15);
        orientBtn.setOnAction(è ->{
            try
            {
                for(int i=IndexNave;i<navi.length;i++)
                    navi[i].orientamento=!navi[i].orientamento;
               response.setText("Posiziona le navi. Orientamento: "+getOrientText(navi[IndexNave].orientamento));
            }
            catch(java.lang.ArrayIndexOutOfBoundsException ex){}
        });
        
        orientBtn.setPrefWidth(70);
        orientBtn.setStyle("-fx-background-position: center center; -fx-background-repeat: stretch; -fx-background-size: 20 20;-fx-background-image: url('img/rotation.png');-fx-background-color: linear-gradient(#ff5400, #be1d00);\n-fx-background-radius: 30;-fx-background-insets: 0;-fx-text-fill: white;");
        
        Text l1 = new Text("Your map");
        l1.setLayoutX(230);l1.setLayoutY(15);
        l1.setFill(Color.WHITE);
        
        Text l2 = new Text("Enemy map");
        l2.setLayoutX(700);l2.setLayoutY(15);
        l2.setFill(Color.WHITE);
        
        //inizializzo navi
        navi = new Nave[5];
        int l = 6;
        for(int i=0;i<navi.length;i++)
        {
            //navi.set(i,new Nave(l--));
            navi[i]=new Nave(l--);
            //(navi[i].length);
        }
         //(navi[0].length);
        
        //inizializzo bottoni
        map = new Ship[10][10];
        enmap = new Ship[10][10];
        
        for(int j=0;j<map.length;j++) 
        {
            for(int k=0;k<map.length;k++) 
            {
                map[j][k] = new Ship(0,j,k);
                GridPane.setConstraints(map[j][k], j, k+1);
                grid.getChildren().add(map[j][k]);
                int riga=map[j][k].r;
                int colonna=map[j][k].c;
                //(riga+" "+colonna);
                map[j][k].setOnAction(e -> selectShip(riga,colonna));
              // System.out.print(riga+" "+colonna+" - ");
            }
              //  ();
        }
 
        
       for(int j=0;j<enmap.length;j++) 
        {
            for(int k=0;k<enmap.length;k++) 
            {
                enmap[j][k] = new Ship(0,j,k);
                //enmap[j][k].setStyle("-fx-background-color:#66ccff;");
                enmap[j][k].setDisable(true);
                GridPane.setConstraints(enmap[j][k], j+11, k+1);
                grid.getChildren().add(enmap[j][k]);
                int riga=enmap[j][k].r;
                int colonna=enmap[j][k].c;
                int a = j;
                int b = k;
                //(riga+" "+colonna);
                enmap[j][k].setOnAction(e -> sendMove(riga,colonna,a,b));
              // System.out.print(riga+" "+colonna+" - ");
            }
              //  ();
        }
       
       response= new TextField();
       response.setText("Posiziona le navi. Orientamento: orizzontale");
       response.setPrefSize(400,30);
       response.setLayoutX(300); response.setLayoutY(500);
       response.setEditable(false);
       response.setFocusTraversable(false);
       response.setStyle("-fx-border-radius: 0 0 0 0;-fx-border-wodth:0;-fx-background-color:black; -fx-text-fill: white;-fx-background-insets: 0;-fx-faint-focus-color: transparent; -fx-focus-color: transparent;");
       response.setAlignment(Pos.CENTER);
       
       grid.getChildren().addAll(orientBtn);
       
       root.getChildren().addAll(grid,response,l1,l2);
       
       
        
        scene = new Scene(root, 1000, 600);
        //scene.setStyle("-fx-background-color:grey;");
        //scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm()); //aggiunta file css
        primaryStage.setScene(scene);
        primaryStage.setTitle("Battaglia Navale 1.0 client");
        
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e ->
        {
            exitGame();
        });
        primaryStage.getIcons().add(new Image("img/icon.png"));
        primaryStage.show();
    } 
    //controlla il posizionamento delle navi all'inizio
    private void selectShip(int r, int c)
    {
        int i = IndexNave;
        //setOrientText(navi[i].orientamento);
        response.setText("Posiziona le navi. Orientamento: "+getOrientText(navi[i].orientamento));
        boolean ok = true;
       //(navi[i].orientamento+" - "+i+" - "+r+" - "+c);
        if (navi[i].orientamento)
        {
            for(int col = c;col<c+navi[i].length;col++)
            {
                try
                {
                    if(map[r][col].getState() != 0) {ok=false;}
                }
                catch(java.lang.ArrayIndexOutOfBoundsException ex){}
                catch(Exception e){ok=false;}
            }
        }
        else
        {
            for(int riga = r;riga<r+navi[i].length;riga++)
            {
                try
                {
                   //(map[riga][c].getState() );
                   if(map[riga][c].getState() != 0) ok=false;
                }
                catch(java.lang.ArrayIndexOutOfBoundsException ex){}
                catch(Exception e){ok=false;}
            }
        }
        if(ok)
        {
            navi[i].r = r;
            navi[i].c = c;
            if(navi[i].positionShip())
                IndexNave++;
            if(IndexNave>=5)
            {
                //connessione
                state = 1;
                stateHandler();
            }
        }
    }
    
    public static String getOrientText(boolean b)
    {
        if(!b)
            return "orizzontale";
        else
           return "verticale";
    }
    
    private static void mapOnAction()
    {
        for(int j=0;j<map.length;j++) 
        {
            for(int k=0;k<map.length;k++) 
            {
                map[j][k].setOnAction(e -> {});
            }
        }
    }
   

    private void connect() 
    {
        
        mapOnAction();
        try
        {
            
            client = new Socket("localhost",8080);
            
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            
            //turn = Integer.parseInt(in.readUTF());
            //new Thread(new Read()).start();
            ReadThread = new Thread(new Read());
            ReadThread.start();
            orientBtn.setDisable(true);
            
            
         
        }catch(Exception e){}
            
               timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            if(!message.equals(""))
                            {
                                if(message.equals("win"))
                                {
                                     timer.cancel();
                                    winAlert();
                                   
                                }
                                else
                                //("Weeeeeeeeeeeeeee "+message);
                                switch(message.split("-")[0])
                                {
                                    case "3": readMove(message);
                                        break;
                                    case "2": readResponse(message);
                                        break;
                                    case "1":
                                        turn = Integer.parseInt(message.split("-")[1]);
                                        if(turn==1)//mio turno -> disabilito mappa + mio turno
                                        {   b1=true;b2=false;
                                            response.setText("E' il tuo turno. Seleziona il bersaglio!");
                                            state=2;
                                        }
                                        else
                                        {//leggo turno altro client
                                            b1=true;b2=true;
                                            response.setText("E' il turno dell'avversario.");
                                            state=3;
                                        }
                                        stateHandler();
                                        break;
                                }
                                
                                message="";
                            }
                   
                        });
                    }
                }, 1000, 50);
                
            response.setText("In attesa della connessione di un avversario...");
            // And From your main() method or any other method
           /* Timer timer = new Timer();
            timer.schedule(new Read(), 0, 500);*/
            
            
        }
    
    private static void sendMove(int riga, int colonna,int j,int k) 
    {
        try 
        {
            RIGA=riga;COLONNA=colonna;
            out.writeUTF("3-"+riga+"-"+colonna);
            enmap[j][k].setOnAction(e ->{});
            //attendo risposta dal nemico
            
            
        } catch (IOException ex){}
        
    }
    public static void readResponse(String msg)
    {
            String[] s = msg.split("-");
            
            if(s[1].equals("N"))
            {
                enmap[RIGA][COLONNA].setState(2);
                response.setText("Nave colpita a : "+RIGA+" - "+COLONNA+"!");
               
            }
            else
            {
                enmap[RIGA][COLONNA].setState(3);
                response.setText("Nave mancata a : "+RIGA+" - "+COLONNA+"!");
            }
            b1=true;b2=true;
            disableMap();
            //state=3;//leggo turno altro client
            //stateHandler();
    }
    void readMove(String txt) 
    {
        
        try 
        {
            String s = txt;
            String[] sp = s.split("-");
            int r =Integer.parseInt(sp[1]);
            int c = Integer.parseInt(sp[2]);
            if(map[r][c].getState()==1)
            {
                map[r][c].setState(2);
             
                if(isWin())
                {
                    out.writeUTF("win");
                    
                   loseAlert();
                }
                else
                    out.writeUTF("2-N");
                response.setText("Il nemico ha colpito la tua nave a : "+r+" - "+c+"!");
            }
            else
            {
                map[r][c].setState(3);
                out.writeUTF("2-X-L");
                response.setText("Il nemico ha mancato il bersaglio, sparando a : "+r+" - "+c+"!");
            }
            
            state=2;
            b1=true;b2=false;
            disableMap();
            
           
            
        } catch (IOException ex) 
        {
         
        }
        
        
    }
    
    private void loseAlert()
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("You lost!");
        alert.setHeaderText("You lost!");
        alert.setContentText("Hai perso!");
        alert.setOnCloseRequest(e ->{exitGame();});
        alert.getDialogPane().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("img/icon.png"));
        alert.showAndWait();
        
    }
    
    private void winAlert()
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("You won!");
        alert.setHeaderText("You Won!");
        alert.setContentText("Hai vinto!");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("img/icon.png"));
        alert.setOnCloseRequest(e ->{exitGame();});
        alert.showAndWait();
    }
    
    //se l'AVVERSARIO ha vinto
    private static boolean isWin()
    {
        boolean win = true;
        for(Ship[] k:map)
        {
            for(Ship o:k)
            {
                if(o.getState()==1)
                    win=false;
            }
        }
        return win;
    }
    
    private static void disableMap() 
    {
        for(int r=0;r<map.length;r++)
        {
            for(int c=0;c<map.length;c++)
            {
                map[r][c].setDisable(b1);
                enmap[r][c].setDisable(b2);
            }
        }
    }
    
    private void state3()
    {
        disableMap();
        state=4;//mio turno
        stateHandler();
    }
    
    private static void exitGame()
    {
        if(timer!=null)
                timer.cancel();
            if(ReadThread!=null)
                ReadThread.interrupt();
            System.exit(0);
    }
      
    public static void main(String[] args) 
    {
        launch(args);
        
    }
}


class Read extends Thread
{
    @Override
    public void run()
    {
        while(true)
        {
            try 
            {
               
                BattagliaNavale.message = in.readUTF();
               
            } catch (IOException ex) 
            {
            }
        }
    }
}