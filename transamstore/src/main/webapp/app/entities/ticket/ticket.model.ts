import { TicketType } from 'app/entities/enumerations/ticket-type.model';

export interface ITicket {
  id: number;
  name?: string | null;
  description?: string | null;
  price?: number | null;
  productSize?: keyof typeof TicketType | null;
  image?: string | null;
  imageContentType?: string | null;
}

export type NewTicket = Omit<ITicket, 'id'> & { id: null };
