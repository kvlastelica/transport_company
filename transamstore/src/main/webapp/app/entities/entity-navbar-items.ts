import NavbarItem from 'app/layouts/navbar/navbar-item.model';

export const EntityNavbarItems: NavbarItem[] = [
  {
    name: 'Customer',
    route: '/customer',
    translationKey: 'global.menu.entities.customer',
  },
  {
    name: 'Ticket',
    route: '/ticket',
    translationKey: 'global.menu.entities.ticket',
  },
  {
    name: 'TicketOrder',
    route: '/ticket-order',
    translationKey: 'global.menu.entities.ticketOrder',
  },
  {
    name: 'OrderItem',
    route: '/order-item',
    translationKey: 'global.menu.entities.orderItem',
  },
];
