
import { environment } from '../environments/environment';
import { AuthenticationService } from './shared/authentication.service';
import { InjectableRxStompConfig } from '@stomp/ng2-stompjs';

export class RxStompConfig extends InjectableRxStompConfig {

  constructor(private authenticationService: AuthenticationService) {
    super();

    this.brokerURL = environment.webSocketEndPoint;

    // How often to heartbeat?
    // Interval in milliseconds, set to 0 to disable
    this.heartbeatIncoming = 0;
    this.heartbeatOutgoing = 20000;

    this.reconnectDelay = 50000;

    // subscribe to authentication service
    this.beforeConnect = (client: any): Promise<void> => {
      return new Promise<void>((resolve, reject) => {
          this.authenticationService.loginState.subscribe(token => {
              if (token) {
                  client.connectHeaders = { 'X-Authorization': sessionStorage.getItem('token') };
                  resolve();
              }
          });
      });
    };

    console.log(environment.webSocketEndPoint);
  }
}
