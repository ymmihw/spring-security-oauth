import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Cookie } from 'ng2-cookies';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { map, catchError  } from 'rxjs/operators';

export class Foo {
  constructor(
    public id: number,
    public name: string) { }
}

@Injectable()
export class AppService {
  constructor(
    private _router: Router, private _http: Http) { }

  obtainAccessToken(loginData) {
    let params = new URLSearchParams();
    params.append('username', loginData.username);
    params.append('password', loginData.password);
    params.append('grant_type', 'password');
    params.append('client_id', 'fooClientId');
    let h = { 'Content-type': 'application/x-www-form-urlencoded; charset=utf-8' };
    let headers = new Headers(h);
    let options = new RequestOptions({ headers: headers });
    console.log(params.toString());
    this._http.post('/oauth/token', params.toString(), options).pipe(
      map(res => res.json()))
      .subscribe(
        data => this.saveToken(data),
        () => alert('Invalid Credentials')
      );
  }


  saveToken(token) {
    let expireDate = new Date().getTime() + (1000 * token.expires_in);
    Cookie.set("access_token", token.access_token, expireDate);
    console.log('Obtained Access token');
    this._router.navigate(['/']);
  }

  getResource(resourceUrl): Observable<Foo> {
    let headers = new Headers({ 'Content-type': 'application/x-www-form-urlencoded; charset=utf-8', 'Authorization': 'Bearer ' + Cookie.get('access_token') });
    let options = new RequestOptions({ headers: headers });
    return this._http.get(resourceUrl, options).pipe(
      map((res: Response) => res.json()),
      catchError((error: any) => Observable.throw(error.json().error || 'Server error')));
  }

  checkCredentials() {
    if (!Cookie.check('access_token')) {
      this._router.navigate(['/login']);
    }
  }

  logout() {
    Cookie.delete('access_token');
    this._router.navigate(['/login']);
  }
}