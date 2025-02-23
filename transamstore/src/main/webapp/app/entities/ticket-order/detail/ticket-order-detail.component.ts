import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ITicketOrder } from '../ticket-order.model';

@Component({
  selector: 'jhi-ticket-order-detail',
  templateUrl: './ticket-order-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class TicketOrderDetailComponent {
  ticketOrder = input<ITicketOrder | null>(null);

  previousState(): void {
    window.history.back();
  }
}
