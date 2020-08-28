import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { map } from 'rxjs/operators';
import { ReplaySubject } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  public loginStateSubject = new ReplaySubject<boolean>(1);
  public loginState = this.loginStateSubject.asObservable();

  public isLoggedIn = false;

  constructor(private httpClient: HttpClient,
              private router: Router) {
    this.loginState.subscribe(x => {
      this.isLoggedIn = x;
    });

    const user = sessionStorage.getItem('username');
    if (user !== null) {
      this.loginStateSubject.next(true);

      this.httpClient.get<any>(environment.serviceUrl + '/validate').subscribe(success => {
        this.loginStateSubject.next(true);
      }, error => {
        this.loginStateSubject.next(false);
      });
    }
  }

  authenticate(username, password) {
    return this.httpClient.post<any>(environment.serviceUrl + '/authenticate', { username, password }).pipe(
      map(
        userData => {
          sessionStorage.setItem('username', username);

          const tokenStr = 'Bearer ' + userData.token;
          sessionStorage.setItem('token', tokenStr);

          this.loginStateSubject.next(true);

          return userData;
        }
      )
    );
  }

  logOut() {
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('token');

    this.loginStateSubject.next(false);

    // reroute to login
    this.router.navigate(['/login']);
  }
}
