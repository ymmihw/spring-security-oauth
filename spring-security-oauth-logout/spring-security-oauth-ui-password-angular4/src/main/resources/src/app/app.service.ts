import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {Cookie} from 'ng2-cookies';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';

export class Foo {
  constructor(
    public id: number,
    public name: string) {}
}

@Injectable()
export class AppService {
  constructor(
    private _router: Router, private _http: Http) {}

  obtainAccessToken(loginData) {
    let params = new URLSearchParams();
    params.append('username', loginData.username);
    params.append('password', loginData.password);
    params.append('grant_type', 'password');
    params.append('client_id', 'fooClientIdPassword');
    let h = {'Content-type': 'application/x-www-form-urlencoded; charset=utf-8'};
    let headers = new Headers(h);
    let options = new RequestOptions({headers: headers});
    console.log(params.toString());
    this._http.post('/oauth/token', params.toString(), options)
      .map(res => res.json())
      .subscribe(
      data => this.saveToken(data),
      err => alert('Invalid Credentials')
      );
  }


  saveToken(token) {
    let expireDate = new Date().getTime() + (1000 * token.expires_in);
    Cookie.set("access_token", token.access_token, expireDate);
    console.log('Obtained Access token');
    this._router.navigate(['/']);
  }

  getResource(resourceUrl): Observable<Foo> {
    let headers = new Headers({'Content-type': 'application/x-www-form-urlencoded; charset=utf-8'});
    let options = new RequestOptions({headers: headers});
    return this._http.get(resourceUrl, options)
      .map((res: Response) => res.json())
      .catch((error: any) => Observable.throw(error.json().error || 'Server error'));
  }

  checkCredentials() {
    if (!Cookie.check('access_token')) {
      this._router.navigate(['/login']);
    }
  }

  logout() {
    this._http.delete('/oauth/token').subscribe(
      data => {
        Cookie.delete('access_token');
        this._router.navigate(['/login']);
      },
      err => alert(err)
    );
  }
}