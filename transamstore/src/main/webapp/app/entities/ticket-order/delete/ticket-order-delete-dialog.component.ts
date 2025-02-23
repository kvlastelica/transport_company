import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITicketOrder } from '../ticket-order.model';
import { TicketOrderService } from '../service/ticket-order.service';

@Component({
  templateUrl: './ticket-order-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TicketOrderDeleteDialogComponent {
  ticketOrder?: ITicketOrder;

  protected ticketOrderService = inject(TicketOrderService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ticketOrderService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
