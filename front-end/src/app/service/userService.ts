import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {User} from "../model/user";
import {Observable} from "rxjs";

@Injectable({providedIn: 'root'})
export class UserService {
  // httpOptions = {
  //   headers: new HttpHeaders({'Content-Type': 'application/json'})
  // };

  private url = 'http://localhost:8080/api/user';
  private httpOptionsPlain = {
    headers: new HttpHeaders({
      'Accept': 'text/plain',
      'Content-Type': 'text/plain'
    }),
    responseType: 'text' as 'json'
  };
  constructor(private http: HttpClient) {
  }

  login(user : User) : Observable<Response>{
    return this.http.put<Response>(this.url+"/login",user);
  }

  signup(user : User) : Observable<Response>{
    // here you have to be careful of the 400 error : t means that there
    // some validation errors in the server
    return this.http.post<Response>(this.url+"/signUp",user);
  }

  changePassword(userID : number, user : User) : Observable<Response>{
    // here you have to be careful of the 400 error : t means that there
    // some validation errors in the server
    return this.http.put<Response>(this.url+"/changePassword/"+userID,user);
  }

  getUserByCredential(credential : string) : Observable<User>{
    return this.http.get<User>(this.url+"/getUserByCredential/"+credential);
  }

  sendCode(credential:string) : Observable<any>{
    return this.http.get<any>(this.url+"/recoverPassword/"+credential,this.httpOptionsPlain);
  }

  sendConfirmationCode(email:string):Observable<string>{
    return this.http.get<string>(this.url+"/confirmEmail/"+email,this.httpOptionsPlain);
  }

  validateAccount(email : string){
    return this.http.get(this.url+"/validateAccount/"+email)
  }

<<<<<<< HEAD
  sendRaportViaEmail(userID : number, startDate:string, endDate:string, takeAll:boolean) : Observable<string>{
    return this.http.get<string>(this.url+"/sendEmailActions/"+userID+"/csv/"+startDate+"/"+endDate+"/"+takeAll)

=======
  sendRaportViaEmail(gsms:Array<number>, userID : number, startDate:string, endDate:string, takeAll:boolean) : Observable<string>{
    return this.http.put<string>(this.url+"/sendEmailActions/"+userID+"/csv/"+startDate+"/"+endDate+"/"+takeAll,gsms,this.httpOptionsPlain)
>>>>>>> 1c8feeb7ed9989c0e4ccb4ebdec6fbb1c46c38f9
  }
}
