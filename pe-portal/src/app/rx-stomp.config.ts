
import { environment } from '../environments/environment';
import { InjectableRxStompConfig } from '@stomp/ng2-stompjs';

export class RxStompConfig extends InjectableRxStompConfig {

  constructor() {
    super();

    this.brokerURL = environment.webSocketEndPoint;

    // How often to heartbeat?
    // Interval in milliseconds, set to 0 to disable
    this.heartbeatIncoming = 0;
    this.heartbeatOutgoing = 20000;

    this.reconnectDelay = 50000;

    console.log(environment.webSocketEndPoint);
  }
}
