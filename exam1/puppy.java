import java.util.Date;

public class puppy{
    String name;
    public puppy(String name){
        // System.out.println(name);
        this.name = name;
    }

    public static void main(String []args){
        puppy a = new puppy("a golden named Vivi");
        System.out.println(a.name);

        int [] b = {1,2,3};
        for(int x: b){
            System.out.println(x);
        } 

        System.out.println('\u039A' );
        Date d = new Date();
        System.out.println(d);

    }
}