import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/shared/authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public username = '';
  public password = '';

  public invalidLogin = false;

  constructor(
    private router: Router,
    private loginService: AuthenticationService
  ) { }

  ngOnInit(): void {
  }

  checkLogin(): void {
    this.loginService.authenticate(this.username, this.password).subscribe(
      data => {
        this.router.navigate(['']);
        this.invalidLogin = false;
      },
      error => {
        this.invalidLogin = true;
      }
    );
  }

}
