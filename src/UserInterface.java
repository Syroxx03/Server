import java.awt.event.WindowListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.io.BufferedReader;
import javax.swing.JTextArea;
import java.net.InetAddress;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Font;
import java.net.URL;
/****/
public class UserInterface implements ActionListener
{
    private Server aServer;
    private JFrame aFrame;
    private JPanel aPanel;
    private JTextArea aClientNbr;
    private JTextArea aConnexionPort;
    private JButton aSearchClient;
    private JButton aOpenServer;
    private JButton aRestartGame;
    /****/
    public UserInterface()
    {
        this.setFrame();
        this.aFrame.setVisible(true);
        this.setPanel();
        this.aFrame.add(this.aPanel);


        this.aServer=new Server(this);
        this.setCloseOperation();

        this.aOpenServer.setEnabled(true);
        this.aRestartGame.setEnabled(true);
    }
    /****/
    public void setClientNbr(final int pClientsNbr)
    {
        try{this.aClientNbr.setText("Nombre de clients: "+pClientsNbr);}
        catch(Error e)
        {}
    }
    /****/
    public void setConnexionPort(final Integer pConnexionPort)
    {
        this.aConnexionPort.setText("Port de connexion: "+pConnexionPort);
    }
    /****/
    @Override public void actionPerformed(final ActionEvent pEvent)
    {
        JButton vButton=(JButton)pEvent.getSource();
        switch(vButton.getText())
        {
            case "Serveur fermé " : this.openServer();break;
            case "Serveur ouvert": this.closeServer();break;
            case "<html><center>Recherche de<br>clients désactivée</html>": this.searchClient();break;
            case "<html><center>Recherche de<br>clients activée</html>":  this.unsearchClient();break;
            case "Restart Game": System.out.println("1"); this.aServer.restartGame(); System.out.println("2");break;
        }
    }
    /****/
    private void openServer()
    {
        if(this.aServer.openServer())
        {
            this.aOpenServer.setText("Serveur ouvert");
            this.aOpenServer.setBackground(Color.green);
            this.aSearchClient.setEnabled(true);
        }
    }
    /****/
    private void closeServer()
    {
        this.aSearchClient.setText("<html><center>Recherche de<br>clients désactivée</html>");
        this.aSearchClient.setBackground(Color.red);
        this.aServer.unsearchClient();
        if(this.aServer.closeServer())
        {
            this.aOpenServer.setText("Serveur fermé ");
            this.aOpenServer.setBackground(Color.red);
            this.aSearchClient.setEnabled(false);
        }
    }
    /****/
    private void searchClient()
    {
        this.aSearchClient.setText("<html><center>Recherche de<br>clients activée</html>");
        this.aSearchClient.setBackground(Color.green);
        this.aServer.searchClient();
    }
    /****/
    private void unsearchClient()
    {
        this.aSearchClient.setText("<html><center>Recherche de<br>clients désactivée</html>");
        this.aSearchClient.setBackground(Color.red);
        this.aServer.unsearchClient();
    }
    /****/
    private void setFrame()
    {
        this.aFrame=new JFrame("Server");
        this.aFrame.setLocation(0,0);
        this.aFrame.setSize(221,267);
        this.aFrame.setAlwaysOnTop(true);

    }
    /****/
    private void setCloseOperation()
    {
        Server vServer = this.aServer;
        WindowListener vWindowListener = new java.awt.event.WindowAdapter()
        {
            @Override public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                vServer.closeServer();
                System.exit(0);
            }
        }
                ;
        this.aFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.aFrame.addWindowListener(vWindowListener);
    }
    /****/
    private void setPanel()
    {
        this.aPanel=new JPanel();
        this.aPanel.setBackground(new Color(20,20,30));
        //Serveur ouvert/fermé
        this.aOpenServer = this.getButton("Serveur fermé ");

        this.aPanel.add(this.aOpenServer);
        //Recherche de clients activée/désactivée
        this.aSearchClient = this.getButton("<html><center>Recherche de<br>clients désactivée</html>");
        this.aPanel.add(this.aSearchClient);
        //Nbr de clients connectés
        this.aClientNbr = this.getTextArea("Nombre de clients: 0");
        this.aPanel.add(this.aClientNbr);
        //Port de connexion
        this.aConnexionPort = this.getTextArea("Port de connexion: "+null);
        this.aPanel.add(this.aConnexionPort);
        //Ip local
        try{this.aPanel.add(this.getTextArea("Ip local:  " + InetAddress.getLocalHost().getHostAddress()));}
        catch(Exception e)
        {e.printStackTrace();}

        this.aRestartGame = this.getButton("Restart Game");
        this.aPanel.add(this.aRestartGame);

        //Ip publique
        // this.aPanel.add(this.getTextArea("Ip public: "+this.getPublicIp()));
    }
    /****/
    private JButton getButton(final String pText)
    {
        JButton vButton=new JButton(pText);
        vButton.setFont(new Font("Arial",Font.BOLD,15));
        vButton.addActionListener(this);
        vButton.setBackground(Color.red);
        vButton.setEnabled(false);
        return vButton;
    }
    /****/
    private JTextArea getTextArea(final String pText)
    {
        JTextArea vTextArea=new JTextArea(pText);
        vTextArea.setEditable(false);
        vTextArea.setForeground(Color.white);
        vTextArea.setBackground(new Color(20,20,30));
        vTextArea.setFont(new Font("Arial",Font.BOLD,15));
        return vTextArea;
    }
    /****/
    private String getPublicIp()
    {
        try
        {
            URLConnection vURLCnx = (new URL("http://checkip.amazonaws.com/")).openConnection();
            return (new BufferedReader(new InputStreamReader(vURLCnx.getInputStream()))).readLine();
        }
        catch(Exception e)
        {e.printStackTrace();return null;}
    }
}