import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {User} from "../model/user";
import {Observable} from "rxjs";

@Injectable({providedIn: 'root'})
export class UserService {
  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  private url = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient) {
  }

  login(user : User) : Observable<Response>{
    return this.http.put<Response>(this.url+"/login",user,this.httpOptions);
  }

  signup(user : User) : Observable<Response>{
    // here you have to be careful of the 400 error : t means that there
    // some validation errors in the server
    return this.http.post<Response>(this.url+"/signUp",user,this.httpOptions);
  }

  changePassword(userID : number, user : User) : Observable<Response>{
    // here you have to be careful of the 400 error : t means that there
    // some validation errors in the server
    return this.http.put<Response>(this.url+"/changePassword/"+userID,user,this.httpOptions);
  }
}
