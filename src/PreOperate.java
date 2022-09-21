import java.util.HashMap;
import java.util.Set;

public class PreOperate {
    public static String removeComments(String str){
        HashMap<String,String> couple=new HashMap<>();
//        couple.put("\"", "\"");
//        couple.put("\'", "\'");
        couple.put("//", "\n");
        couple.put("/*", "*/");
        Set<String> fronts=couple.keySet();

        String after="";
        int temp=0;
        while(temp<str.length()) {
            int min=-1;
            String head="";
            for(String front:fronts) {
                int indx=str.indexOf(front,temp);
                if(min<0||(indx>=0&&indx<min)) {
                    min=indx;
                    head=front;
                }
//				System.out.println(front+"  ("+str.indexOf(front,temp));
            }
            String end=couple.get(head);
//			System.out.println(head+","+end);
            if(min<0) {
                after+=str.substring(temp);
                temp=str.length();
            }
            else {
                after+=str.substring(temp,min);
//				System.out.println(str.substring(temp,min));
                temp=min;
                int indx=str.indexOf(end,temp+head.length());
                if(indx==-1)
                    indx=str.length();
                if(head.charAt(0)!='/') {
//    				System.out.println(str.substring(temp,indx+end.length()));
                    after+=str.substring(temp,indx+end.length());
                }
                temp=indx+end.length();
            }
        }
        return after;
    }
}
